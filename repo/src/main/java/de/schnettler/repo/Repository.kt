package de.schnettler.repo

import com.dropbox.android.external.store4.*
import de.schnettler.database.AppDatabase
import de.schnettler.database.models.*
import de.schnettler.lastfm.api.RetrofitService
import de.schnettler.repo.authentication.AccessTokenAuthenticator
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.authentication.provider.SpotifyAuthProvider
import de.schnettler.repo.mapping.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
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

    //fun getTopArtists() = topArtistStore.stream(StoreRequest.cached("1", true))

//    private val topArtistStore = StoreBuilder.from(
//        fetcher = nonFlowValueFetcher {
//            TopListMapper.forLists().invoke(service.getTopArtists())
//        },
//        sourceOfTruth = SourceOfTruth.from(
//            reader = {
//                db.chartDao().getTopArtists("TOP_LIST_ARTIST").mapLatest { entry -> entry?.map { it.artist } }
//            },
//            writer = { _: String, listEntry: List<ListEntryWithArtist> ->
//                db.chartDao().insertTopArtists(listEntry)
//            }
//        )
//    ).build()

    fun getUserInfo(): Flow<StoreResponse<User>> {
        val userInfoStore = StoreBuilder.from<String, User>(
            fetcher = nonFlowValueFetcher {
                UserMapper.map(service.getUserInfo(lastFmAuthProvider.getSession().key))
            }
        ).build()
        return userInfoStore.stream(StoreRequest.fresh(""))
    }

    fun getTopArtists(type: TopListEntryType): Flow<StoreResponse<List<Artist>>> {
        val userInfoStore = StoreBuilder.from (
            fetcher = nonFlowValueFetcher {entryType: TopListEntryType ->
                return@nonFlowValueFetcher when(entryType) {
                    TopListEntryType.USER_ARTIST -> {
                        val artists = ArtistMinMapper.forLists().invoke(service.getUserTopArtists(lastFmAuthProvider.getSession().key))
                        artists.forEach { artist ->
                            refreshImageUrl(db.artistDao().getArtistImageUrl(artist.id), artist)
                        }
                        artists
                    }
                    TopListEntryType.CHART_ARTIST -> {
                        ArtistMinMapper.forLists().invoke(service.getTopArtists())
                    }
                    else -> listOf()
                }

            },
            sourceOfTruth = SourceOfTruth.from(
                reader = {entryType: TopListEntryType ->
                    db.chartDao().getTopArtists(entryType).map { list -> list.map { it.artist } }
                },
                writer = {entryType: TopListEntryType, artists: List<Artist> ->
                    db.artistDao().insertEntitiesWithTopListEntries(
                        artists,
                        TopListMapper.forLists().invoke(artists.map { Pair(it, entryType) })
                    )
                }
            )
        ).build()
        return userInfoStore.stream(StoreRequest.cached(type, true))
    }

    fun getUserTopAlbums(): Flow<StoreResponse<List<Album>>> {
        val userInfoStore = StoreBuilder.from<String, List<Album>>(
            fetcher = nonFlowValueFetcher {
                AlbumMapper.forLists().invoke(service.getUserTopAlbums(lastFmAuthProvider.getSession().key))
            }
        ).build()
        return userInfoStore.stream(StoreRequest.fresh(""))
    }

    fun getUserTopTracks(): Flow<StoreResponse<List<Track>>> {
        val userInfoStore = StoreBuilder.from<String, List<Track>>(
            fetcher = nonFlowValueFetcher {
                TrackMapper.forLists().invoke(service.getUserTopTracks(lastFmAuthProvider.getSession().key))
            }
        ).build()
        return userInfoStore.stream(StoreRequest.fresh(""))
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
            val response = service.getArtistInfo(key)
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