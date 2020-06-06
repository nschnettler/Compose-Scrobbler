package de.schnettler.repo

import com.dropbox.android.external.store4.*
import de.schnettler.common.TimePeriod
import de.schnettler.database.AppDatabase
import de.schnettler.database.models.*
import de.schnettler.lastfm.api.RetrofitService
import de.schnettler.repo.authentication.AccessTokenAuthenticator
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.authentication.provider.SpotifyAuthProvider
import de.schnettler.repo.mapping.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
@FlowPreview
class Repository(private val db: AppDatabase, coroutineScope: CoroutineScope) {
    //Services
    private val service = RetrofitService.lastFmService

    //Authentication Providers
    private val spotifyAuthProvider =
        SpotifyAuthProvider(
            RetrofitService.spotifyAuthService,
            db.authDao()
        )
    val lastFmAuthProvider =
        LastFmAuthProvider(
            RetrofitService.lastFmService,
            db.authDao(),
            coroutineScope
        )

    //Authenticators
    private val spotifyAuthenticator =
        AccessTokenAuthenticator(
            spotifyAuthProvider,
            coroutineScope.coroutineContext
        )

    private suspend fun provideSpotify() =
        RetrofitService.provideAuthenticatedSpotifyService(
            spotifyAuthProvider.getToken().token,
            authenticator = spotifyAuthenticator)

    private val userStore = StoreBuilder.from(
        fetcher = nonFlowValueFetcher {session: Session ->
            UserMapper.map(service.getUserInfo(session.key))
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = {session: Session ->
                db.userDao().getUser(session.name)
            },
            writer = { session: Session, user: User ->
                val oldUser = db.userDao().getUserOnce(session.name)
                oldUser?.let {
                    user.artistCount = it.artistCount
                    user.lovedTracksCount = it.lovedTracksCount
                }
                db.userDao().forceInsert(user)
            }
        )
    ).build()

    fun getUserInfo(): Flow<StoreResponse<User>> {
        return userStore.stream(StoreRequest.cached(lastFmAuthProvider.session!!,true))
    }

    fun getUserLovedTracks(): Flow<StoreResponse<List<Track>>> {
        return StoreBuilder.from(
            fetcher = nonFlowValueFetcher {_: String ->
                val session = lastFmAuthProvider.session!!
                val result = service.getUserLikedTracks(session.key)
                db.userDao().updateLovedTracksCount(session.name, result.info.total)
                result.track.map { it.map() }
            }
        ).build().stream(StoreRequest.cached("",true))
    }

    fun getTopArtists(timePeriod: TimePeriod) = StoreBuilder.from(
        fetcher = nonFlowValueFetcher {
            val session = lastFmAuthProvider.session
            val response = service.getUserTopArtists(timePeriod, session!!.key)
            val artists = response.artist.map { it.map() }
            db.userDao().updateArtistCount(session.name, response.info.total)
            artists.forEach { artist ->
                refreshImageUrl(db.artistDao().getArtistImageUrl(artist.id), artist)
            }
            artists
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = {
                db.chartDao().getTopArtists(TopListEntryType.USER_ARTIST)
            },
            writer = {_: Any, listings: List<Artist> ->
                val topListEntries = TopListMapper.forLists().invoke(listings.map { Pair(it, TopListEntryType.USER_ARTIST) })
                db.artistDao().insertEntitiesWithTopListEntries(
                    listings, topListEntries
                )
            }
        )
    ).build().stream(StoreRequest.cached("", true))

    fun getTopAlbums(timePeriod: TimePeriod) = StoreBuilder.from(
        fetcher = nonFlowValueFetcher {
            service.getUserTopAlbums(timePeriod, lastFmAuthProvider.session!!.key).map { it.map() }
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = {
                db.chartDao().getTopAlbums(TopListEntryType.USER_ALBUM)
            },
            writer = {_: Any, listings: List<Album> ->
                val topListEntries = TopListMapper.forLists().invoke(listings.map { Pair(it, TopListEntryType.USER_ALBUM) })
                db.albumDao().insertEntitiesWithTopListEntries(
                    listings,
                    topListEntries
                )
            }
        )
    ).build().stream(StoreRequest.cached("", true))

    fun getTopTracks(timePeriod: TimePeriod) = StoreBuilder.from(
        fetcher = nonFlowValueFetcher {
            service.getUserTopTracks(timePeriod, lastFmAuthProvider.session!!.key).map { it.map() }
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = {
                db.chartDao().getTopTracks(TopListEntryType.USER_TRACKS)
            },
            writer = {_: Any, listings: List<Track> ->
                val topListEntries = TopListMapper.forLists().invoke(listings.map { Pair(it, TopListEntryType.USER_TRACKS) })
                db.trackDao().insertEntitiesWithTopListEntries(
                    listings,
                    topListEntries
                )
            }
        )
    ).build().stream(StoreRequest.cached("", true))

    fun getArtistChart(type: TopListEntryType): Flow<StoreResponse<List<Artist>>> {
        val userInfoStore = StoreBuilder.from (
            fetcher = nonFlowValueFetcher { _: String ->
                service.getTopArtists().map { it.map() }

            },
            sourceOfTruth = SourceOfTruth.from(
                reader = {_: String ->
                    db.chartDao().getTopArtists(TopListEntryType.CHART_ARTIST).map { list -> list.map { it.data } }
                },
                writer = {_: String, listings: List<Artist> ->
                    val topListEntries = TopListMapper.forLists().invoke(listings.map { Pair(it, TopListEntryType.CHART_ARTIST) })
                    db.artistDao().insertEntitiesWithTopListEntries(
                        listings,
                        topListEntries
                    )
                }
            )
        ).build()
        return userInfoStore.stream(StoreRequest.cached("", true))
    }

    fun getUserRecentTrack(): Flow<StoreResponse<List<Track>>> {
        val userInfoStore = StoreBuilder.from<String, List<Track>>(
            fetcher = nonFlowValueFetcher {
               service.getUserRecentTrack(lastFmAuthProvider.session!!.key).map { it.map() }
            }
        ).build()
        return userInfoStore.stream(StoreRequest.fresh(""))
    }

    fun getArtistInfo(id: String) = StoreBuilder.from(
        fetcher = nonFlowValueFetcher { key: String ->
            val response = service.getArtistInfo(key, lastFmAuthProvider.session!!.key)
            val artist = response.map()
            refreshImageUrl(db.artistDao().getArtistImageUrl(key), artist)
            artist.topAlbums = service.getArtistAlbums(key).map { it.map() }
            artist.topTracks = service.getArtistTracks(key).map { it.map() }
            artist.similarArtists = response.similar.artist.map { it.mapToArtist() }
            artist
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = {key: String ->
                var artist = db.artistDao().getArtist(key)
                artist = artist.combine(db.relationshipDao().getRelatedAlbums(key, ListingType.ARTIST)) {artist, albums ->
                    artist?.topAlbums = albums.map { it.album }
                    return@combine artist
                }.combine(db.relationshipDao().getRelatedTracks(key, ListingType.ARTIST)) {artist, tracks ->
                    artist?.topTracks = tracks.map { it.track }
                    return@combine artist
                }.combine(db.relationshipDao().getRelatedArtists(key, ListingType.ARTIST)) {artist, artists ->
                    artist?.similarArtists = artists.map { it.artist }
                    return@combine artist
                }
                artist
            },
            writer = { key: String, artist: Artist ->
                //Make sure to not overwrite the already saved ImageUrl
                val oldImageUrl = db.artistDao().getArtistImageUrl(key)
                oldImageUrl?.let {
                    artist.imageUrl = oldImageUrl
                }
                db.artistDao().forceInsert(artist)
                //Tracks
                db.trackDao().insertEntriesWithRelations(
                    artist.topTracks,
                    RelationMapper.forLists().invoke(artist.topTracks.map { Pair(artist, it) })
                )
                //Albums
                db.albumDao().insertEntriesWithRelations(
                    artist.topAlbums,
                    RelationMapper.forLists().invoke(artist.topAlbums.map { Pair(artist, it) })
                )
                //Artist
                db.artistDao().insertEntriesWithRelations(
                    artist.similarArtists,
                    RelationMapper.forLists().invoke(artist.similarArtists.map { Pair(artist, it) }))
            }
        )
    ).build().stream(StoreRequest.cached(id, true))

    fun getTrackInfo(track: Track) = StoreBuilder.from(
        fetcher = nonFlowValueFetcher { track: Track ->
            val result = service.getTrackInfo(track.artist, track.name, lastFmAuthProvider.session!!.key).map()
            refreshImageUrl(db.trackDao().getTrackImageUrl(result.id), result)
            result
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = {key ->
                db.trackDao().getTrack(key.id, key.artist)
            },
            writer = { _: Track, track ->
                db.trackDao().forceInsert(track)
            }
        )
    ).build().stream(StoreRequest.cached(track, true))

    private suspend fun refreshImageUrl(localImageUrl: String?, listing: ListingMin) {
        if (localImageUrl.isNullOrBlank()) {
            println("Refreshing ImageURl for ${listing.name}")
            val url: String? = when (listing) {
                is Artist -> provideSpotify().searchArtist(listing.name).maxBy {
                        item -> item.popularity
                }?.images?.first()?.url
                is Track -> ""
                else -> TODO()
            }
            println("ImageUrl $url")
            url?.let {
                listing.imageUrl = url
            }
        }
    }
}