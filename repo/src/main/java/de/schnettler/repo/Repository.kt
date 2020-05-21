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
                val artists = ArtistMinMapper.forLists().invoke(service.getUserTopArtists(lastFmAuthProvider.getSession().key))
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
        fetcher = nonFlowValueFetcher { key: String ->
            ArtistMapper.map(service.getArtistInfo(key))
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = {key: String ->
                db.artistDao().getArtist(key)
            },
            writer = { _: String, artist: Artist ->
                db.artistDao().insertArtist(artist)
                //TODO: Write similar Artists into Db
            }
        )
    ).build().stream(StoreRequest.cached(name, true))

    fun getArtistAlbums(name: String) = StoreBuilder.from(
        fetcher = nonFlowValueFetcher { key: String ->
            AlbumMapper.forLists().invoke(service.getArtistAlbums(key))
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = {key: String ->
                db.relationshipDao().getRelatedAlbums(key, ListingType.ARTIST)
            },
            writer = { _: String, albums: List<Album> ->
                db.albumDao().insertAlbums(albums)
            }
        )
    ).build().stream(StoreRequest.cached(name, true))

    fun getArtistTracks(artist: ListingMin) = StoreBuilder.from(
        fetcher = nonFlowValueFetcher { key: ListingMin ->
            TrackMapper.forLists().invoke(service.getArtistTracks(key.name))
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = {key: ListingMin ->
                db.relationshipDao().getRelatedTracks(key.name, ListingType.ARTIST)
            },
            writer = {art: ListingMin, tracks: List<Track>->
                val relations = RelationMapper.forLists().invoke(tracks.map { Pair(art, it) })
                db.trackDao().insertTracks(tracks)
                db.relationshipDao().insertRelations(relations)
            }
        )
    ).build().stream(StoreRequest.cached(artist, true))
}