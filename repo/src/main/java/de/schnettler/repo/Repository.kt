package de.schnettler.repo

import android.content.Context
import com.dropbox.android.external.store4.*
import de.schnettler.database.models.*
import de.schnettler.database.provideDatabase
import de.schnettler.lastfm.api.LastFmService
import de.schnettler.lastfm.api.RetrofitService
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

    fun getUserTopArtists(sessionKey: String): Flow<StoreResponse<List<Artist>>> {
        val userInfoStore = StoreBuilder.from<String, List<Artist>>(
            fetcher = nonFlowValueFetcher {
                ArtistMapper.forLists().invoke(service.getUserTopArtists(sessionKey))
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

    fun getArtistTopAlbums(name: String) = StoreBuilder.from(
        fetcher = nonFlowValueFetcher {key: String ->
            AlbumMapper.forLists().invoke(service.getArtistAlbums(key))
        }
    ).build().stream(StoreRequest.fresh(name))
}