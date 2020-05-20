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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
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

    fun getTopArtists() = topArtistStore.stream(StoreRequest.cached("1", true))

    private val topArtistStore = StoreBuilder.from(
        fetcher = nonFlowValueFetcher {
            TopListMapper.forLists().invoke(service.getTopArtists())
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = {
                db.chartDao().getTopArtists("TOP_LIST_ARTIST").mapLatest { entry -> entry?.map { it.artist } }
            },
            writer = { _: String, listEntry: List<ListEntryWithArtist> ->
                db.chartDao().insertTopArtists(listEntry)
            }
        )
    ).build()

    fun getUserInfo(): Flow<StoreResponse<User>> {
        val userInfoStore = StoreBuilder.from<String, User>(
            fetcher = nonFlowValueFetcher {
                UserMapper.map(service.getUserInfo(lastFmAuthProvider.getSession().key))
            }
        ).build()
        return userInfoStore.stream(StoreRequest.fresh(""))
    }

    fun getUserTopArtists(): Flow<StoreResponse<List<Artist>>> {
        val userInfoStore = StoreBuilder.from<String, List<Artist>>(
            fetcher = nonFlowValueFetcher {
                val artists = ArtistMapper.forLists().invoke(service.getUserTopArtists(lastFmAuthProvider.getSession().key))
                val localService = RetrofitService.provideAuthenticatedSpotifyService(spotifyAuthProvider.getToken().token, authenticator = spotifyAuthenticator)
                artists.forEach { artist ->
                    val imageUrl = localService.searchArtist(artist.name).maxBy { item -> item.popularity }?.images?.first()?.url
                    artist.imageUrl = imageUrl
                }
                return@nonFlowValueFetcher artists
            }
        ).build()
        return userInfoStore.stream(StoreRequest.fresh(""))
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

    fun getArtistInfo(name: String) = StoreBuilder.from(
        fetcher = nonFlowValueFetcher {key: String ->
            val info = ArtistInfoMapper.map(service.getArtistInfo(key))
            val albums = AlbumMapper.forLists().invoke(service.getArtistAlbums(key))
            val tracks = TrackMapper.forLists().invoke(service.getArtistTracks(key))
            info.topAlbums = albums
            info.topTracks = tracks
            return@nonFlowValueFetcher info
        }
    ).build().stream(StoreRequest.fresh(name))


//    fun getArtistImages(artists: List<Artist>?, token: AuthToken?) = StoreBuilder.from(
//        fetcher = nonFlowValueFetcher {_: String ->
//            val localService = RetrofitService.provideAuthenticatedSpotifyService(getValidAuthToken(token).token, spotifyAuthenticator)
//            artists?.map { artist -> localService.searchArtist(artist.name).maxBy { item -> item.popularity }?.images?.first()?.url } ?: listOf()
//        }
//    ).build().stream(StoreRequest.fresh(""))
//
//    private suspend fun getValidAuthToken(token: AuthToken?): AuthToken {
//        return if (token == null || !token.isValid()) {
//            spotifyAuthStore.fresh(AuthTokenType.Spotify.value)
//        } else {
//            token
//        }
//    }
}