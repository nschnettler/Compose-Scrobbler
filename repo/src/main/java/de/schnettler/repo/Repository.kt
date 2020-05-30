package de.schnettler.repo

import com.dropbox.android.external.store4.*
import de.schnettler.database.AppDatabase
import de.schnettler.database.models.*
import de.schnettler.lastfm.api.RetrofitService
import de.schnettler.repo.authentication.AccessTokenAuthenticator
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.authentication.provider.SpotifyAuthProvider
import de.schnettler.repo.mapping.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
@FlowPreview
class Repository(private val db: AppDatabase, context: CoroutineContext) {
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
            db.authDao()
        )

    //Authenticators
    private val spotifyAuthenticator =
        AccessTokenAuthenticator(
            spotifyAuthProvider,
            context
        )

    private suspend fun provideSpotify() =
        RetrofitService.provideAuthenticatedSpotifyService(
            spotifyAuthProvider.getToken().token,
            authenticator = spotifyAuthenticator)

    fun getUserInfo(): Flow<StoreResponse<User>> {
        val currentSession = runBlocking { lastFmAuthProvider.getSession() }

        return StoreBuilder.from(
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
        ).build().stream(StoreRequest.cached(currentSession,true))
    }

    fun getUserLovedTracks(): Flow<StoreResponse<List<Track>>> {
        val currentSession = runBlocking { lastFmAuthProvider.getSession() }

        return StoreBuilder.from(
            fetcher = nonFlowValueFetcher {session: Session ->
                val result = service.getUserLikedTracks(session.key)
                db.userDao().updateLovedTracksCount(session.name, result.info.total)
                TrackMapper.forLists().invoke(result.track)
            }
        ).build().stream(StoreRequest.cached(currentSession,true))
    }

    fun getTopList(type: TopListEntryType): Flow<StoreResponse<List<ListingMin>>> {
        val userInfoStore = StoreBuilder.from (
            fetcher = nonFlowValueFetcher {entryType: TopListEntryType ->
                return@nonFlowValueFetcher when(entryType) {
                    TopListEntryType.USER_ARTIST -> {
                        val session = lastFmAuthProvider.getSession()
                        val response = service.getUserTopArtists(session.key)
                        val artists = ArtistMinMapper.forLists().invoke(response.artist)
                        db.userDao().updateArtistCount(session.name, response.info.total)
                        artists.forEach { artist ->
                            refreshImageUrl(db.artistDao().getArtistImageUrl(artist.id), artist)
                        }
                        artists
                    }
                    TopListEntryType.CHART_ARTIST -> {
                        ArtistMinMapper.forLists().invoke(service.getTopArtists())
                    }
                    TopListEntryType.USER_TRACKS -> {
                        TrackMapper.forLists().invoke(service.getUserTopTracks(lastFmAuthProvider.getSession().key))
                    }
                    TopListEntryType.USER_ALBUM -> {
                        AlbumMapper.forLists().invoke(service.getUserTopAlbums(lastFmAuthProvider.getSession().key))
                    }
                    else -> listOf()
                }

            },
            sourceOfTruth = SourceOfTruth.from(
                reader = {entryType: TopListEntryType ->
                    when(entryType) {
                        TopListEntryType.CHART_ARTIST -> {
                            db.chartDao().getTopArtists(entryType).map { list -> list.map { it.artist } }
                        }
                        TopListEntryType.USER_ARTIST -> {
                            db.chartDao().getTopArtists(entryType).map { list -> list.map { it.artist } }
                        }
                        TopListEntryType.USER_TRACKS -> {
                            db.chartDao().getTopTracks(entryType).map { list -> list.map { it.track } }
                        }
                        TopListEntryType.USER_ALBUM -> {
                            db.chartDao().getTopAlbums(entryType).map { list -> list.map { it.album } }
                        }
                        TopListEntryType.UNDEFINED -> emptyFlow()
                    }
                },
                writer = {entryType: TopListEntryType, listings: List<ListingMin> ->
                    val topListEntries = TopListMapper.forLists().invoke(listings.map { Pair(it, entryType) })
                    when(listings.first()){
                        is Artist -> {
                            db.artistDao().insertEntitiesWithTopListEntries(
                                listings as List<Artist>,
                                topListEntries
                            )
                        }
                        is Track -> {
                            db.trackDao().insertEntitiesWithTopListEntries(
                                listings as List<Track>,
                                topListEntries
                            )
                        }
                        is Album -> {
                            db.albumDao().insertEntitiesWithTopListEntries(
                                listings as List<Album>,
                                topListEntries
                            )
                        }
                    }

                }
            )
        ).build()
        return userInfoStore.stream(StoreRequest.cached(type, true))
    }

    fun getUserRecentTrack(): Flow<StoreResponse<List<Track>>> {
        val userInfoStore = StoreBuilder.from<String, List<Track>>(
            fetcher = nonFlowValueFetcher {
                TrackWithAlbumMapper.forLists().invoke(service.getUserRecentTrack(lastFmAuthProvider.getSession().key))
            }
        ).build()
        return userInfoStore.stream(StoreRequest.fresh(""))
    }

    fun getArtistInfo(id: String) = StoreBuilder.from(
        fetcher = nonFlowValueFetcher { key: String ->
            val response = service.getArtistInfo(key, lastFmAuthProvider.getSession().key)
            val artist = ArtistMapper.map(response)
            refreshImageUrl(db.artistDao().getArtistImageUrl(key), artist)
            artist.topAlbums = AlbumMapper.forLists().invoke(service.getArtistAlbums(key))
            artist.topTracks = TrackMapper.forLists().invoke(service.getArtistTracks(key))
            artist.similarArtists = ArtistMinMapper.forLists().invoke(response.similar.artist)
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

    private suspend fun refreshImageUrl(localImageUrl: String?, artist: Artist) {
        if (localImageUrl.isNullOrBlank()) {
            println("Refreshing ImageURl for ${artist.name}")
            val url = provideSpotify()
                .searchArtist(artist.name).maxBy { item ->
                    item.popularity
                }?.images?.first()?.url
            println("ImageUrl $url")
            url?.let {
                artist.imageUrl = url
            }
        }
    }
}