package de.schnettler.repo

import android.content.Context
import androidx.room.Database
import com.dropbox.android.external.store4.*
import de.schnettler.database.AppDatabase
import de.schnettler.database.models.*
import de.schnettler.database.provideDatabase
import de.schnettler.lastfm.api.LastFmService
import de.schnettler.lastfm.api.RetrofitService
import de.schnettler.lastfm.api.RetrofitService.spotifyService
import de.schnettler.lastfm.api.SpotifyService
import de.schnettler.repo.mapping.*
import de.schnettler.repo.util.createSignature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

@ExperimentalCoroutinesApi
@FlowPreview
class Repository(val db: AppDatabase, scope: CoroutineScope) {

    private val service = RetrofitService.lastFmService
    private val tokenProvider = AccessTokenProvider(this, db.authDao())
    private val spotifyAuthenticator = AccessTokenAuthenticator(tokenProvider, scope)

    fun getTopArtists() = topArtistStore.stream(StoreRequest.cached("1", true))
    fun getSession() = db.authDao().getSession()

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

    suspend fun refreshSession(token: String) {
        val params= mutableMapOf("token" to token)
        val signature = createSignature(LastFmService.METHOD_AUTH_SESSION, params, LastFmService.SECRET)
        val session = SessionMapper.map(service.getSession(token, signature))
        db.authDao().insertSession(session)
    }


    fun getUserInfo(sessionKey: String): Flow<StoreResponse<User>> {
        val userInfoStore = StoreBuilder.from<String, User>(
            fetcher = nonFlowValueFetcher {
                UserMapper.map(service.getUserInfo(sessionKey))
            }
        ).build()
        return userInfoStore.stream(StoreRequest.fresh(""))
    }

    fun getUserTopArtists(sessionKey: String): Flow<StoreResponse<List<Artist>>> {
        val userInfoStore = StoreBuilder.from<String, List<Artist>>(
            fetcher = nonFlowValueFetcher {
                val artists = ArtistMapper.forLists().invoke(service.getUserTopArtists(sessionKey))
                val localService = RetrofitService.provideAuthenticatedSpotifyService(tokenProvider.getNonNullToken().token, authenticator = spotifyAuthenticator)
                artists.forEach { artist ->
                    val imageUrl = localService.searchArtist(artist.name).maxBy { item -> item.popularity }?.images?.first()?.url
                    artist.imageUrl = imageUrl
                }
                return@nonFlowValueFetcher artists
            }
        ).build()
        return userInfoStore.stream(StoreRequest.fresh(""))
    }

    fun getUserTopAlbums(sessionKey: String): Flow<StoreResponse<List<Album>>> {
        val userInfoStore = StoreBuilder.from<String, List<Album>>(
            fetcher = nonFlowValueFetcher {
                AlbumMapper.forLists().invoke(service.getUserTopAlbums(sessionKey))
            }
        ).build()
        return userInfoStore.stream(StoreRequest.fresh(""))
    }

    fun getUserTopTracks(sessionKey: String): Flow<StoreResponse<List<Track>>> {
        val userInfoStore = StoreBuilder.from<String, List<Track>>(
            fetcher = nonFlowValueFetcher {
                TrackMapper.forLists().invoke(service.getUserTopTracks(sessionKey))
            }
        ).build()
        return userInfoStore.stream(StoreRequest.fresh(""))
    }

    fun getUserRecentTrack(sessionKey: String): Flow<StoreResponse<List<Track>>> {
        val userInfoStore = StoreBuilder.from<String, List<Track>>(
            fetcher = nonFlowValueFetcher {
                TrackWithAlbumMapper.forLists().invoke(service.getUserRecentTrack(sessionKey))
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

    suspend fun refreshSpotifyAuthToken() = StoreBuilder.from(
        fetcher = nonFlowValueFetcher {item: String ->
            SpotifyAuthMapper.map(RetrofitService.spotifyAuthService.login(SpotifyService.TYPE_CLIENT))
        }
    ).build().fresh("")

    suspend fun insertAuthToken(token: AuthToken) = db.authDao().insertAuthToken(token)


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