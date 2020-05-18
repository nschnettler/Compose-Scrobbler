package de.schnettler.repo

import android.content.Context
import com.dropbox.android.external.store4.*
import de.schnettler.database.models.*
import de.schnettler.database.provideDatabase
import de.schnettler.lastfm.api.LastFmService
import de.schnettler.lastfm.api.RetrofitService
import de.schnettler.lastfm.api.SpotifyService
import de.schnettler.lastfm.models.SpotifyArtist
import de.schnettler.repo.mapping.*
import de.schnettler.repo.util.createSignature
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

@ExperimentalCoroutinesApi
@FlowPreview
class Repository(context: Context) {

    private val service = RetrofitService.lastFmService
    private val sAuthService = RetrofitService.spotifyAuthService
    private val sService = RetrofitService.spotifyService

    private val db = provideDatabase(context)

    fun getTopArtists() = topArtistStore.stream(StoreRequest.cached("1", true))
    fun getSession() = db.authDao().getSession()

    private val topArtistStore = StoreBuilder.from(
        fetcher = nonFlowValueFetcher {
            TopListMapper.forLists().invoke(service.getTopArtists())
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = {
                db.topListDao().getTopArtists("TOP_LIST_ARTIST").mapLatest { entry -> entry?.map { it.artist } }
            },
            writer = { _: String, listEntry: List<ListEntryWithArtist> ->
                db.topListDao().insertTopArtists(listEntry)
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

    fun getUserTopArtists(sessionKey: String, spotifyToken: AuthToken): Flow<StoreResponse<List<Artist>>> {
        val userInfoStore = StoreBuilder.from<String, List<Artist>>(
            fetcher = nonFlowValueFetcher {
                var token = spotifyToken
                val artists = ArtistMapper.forLists().invoke(service.getUserTopArtists(sessionKey))
                if (!spotifyToken.isValid()) {
                    token = spotifyAuthStore.fresh(AuthTokenType.Spotify.value)
                }
                val localService = RetrofitService.provideAuthenticatedSpotifyService(token.token)
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

    val spotifyAuthStore = StoreBuilder.from (
        fetcher = nonFlowValueFetcher {_: String ->
            SpotifyAuthMapper.map(sAuthService.login(SpotifyService.TYPE_CLIENT))
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = { _: String ->
                db.authDao().getAuthToken(AuthTokenType.Spotify.value)
            },
            writer = { _: String, token ->
                db.authDao().insertAuthToken(token)
            },
            delete = {_: String ->
                db.authDao().deleteAuthToken(AuthTokenType.Spotify.value)
            }
        )
    ).build()

    val getSpotifyAuthToken = spotifyAuthStore.stream(StoreRequest.cached("null", false))
}