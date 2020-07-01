package de.schnettler.repo

import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.nonFlowValueFetcher
import de.schnettler.common.TimePeriod
import de.schnettler.database.daos.*
import de.schnettler.database.models.*
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.authentication.AccessTokenAuthenticator
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.authentication.provider.SpotifyAuthProvider
import de.schnettler.repo.mapping.TopListMapper
import de.schnettler.repo.mapping.forLists
import de.schnettler.repo.mapping.map
import de.schnettler.repo.util.provideSpotifyService
import javax.inject.Inject

class TopListRepository @Inject constructor(
    private val userDao: UserDao,
    private val artistDao: ArtistDao,
    private val albumDao: AlbumDao,
    private val trackDao: TrackDao,
    private val chartDao: ChartDao,
    private val service: LastFmService,
    private val authProvider: LastFmAuthProvider,
    private val spotifyAuthProvider: SpotifyAuthProvider,
    private val spotifyAuthenticator: AccessTokenAuthenticator
) {
    fun getTopArtists(timePeriod: TimePeriod) = StoreBuilder.from(
        fetcher = nonFlowValueFetcher {
            val session = authProvider.session
            val response = service.getUserTopArtists(timePeriod, session!!.key)
            val artists = response.artist.map { it.map() }
            userDao.updateArtistCount(session.name, response.info.total)
            artists.forEach { artist ->
                refreshImageUrl(artistDao.getArtistImageUrl(artist.id), artist)
            }
            artists
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = {
                chartDao.getTopArtists(TopListEntryType.USER_ARTIST)
            },
            writer = { _: Any, listings: List<Artist> ->
                val topListEntries = TopListMapper.forLists()
                    .invoke(listings.map { Pair(it, TopListEntryType.USER_ARTIST) })
                artistDao.insertEntitiesWithTopListEntries(
                    listings, topListEntries
                )
            }
        )
    ).build().stream(StoreRequest.cached("", true))


    fun getTopAlbums(timePeriod: TimePeriod) = StoreBuilder.from(
        fetcher = nonFlowValueFetcher {
            service.getUserTopAlbums(timePeriod, authProvider.session!!.key).map { it.map() }
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = {
                chartDao.getTopAlbums(TopListEntryType.USER_ALBUM)
            },
            writer = { _: Any, listings: List<Album> ->
                val topListEntries = TopListMapper.forLists()
                    .invoke(listings.map { Pair(it, TopListEntryType.USER_ALBUM) })
                albumDao.insertEntitiesWithTopListEntries(
                    listings,
                    topListEntries
                )
            }
        )
    ).build().stream(StoreRequest.cached("", true))

    fun getTopTracks(timePeriod: TimePeriod) = StoreBuilder.from(
        fetcher = nonFlowValueFetcher {
            val tracks =
                service.getUserTopTracks(timePeriod, authProvider.session!!.key).map { it.map() }
            tracks.forEach { track ->
                refreshImageUrl(trackDao.getTrackImageUrl(track.id), track)
            }
            tracks
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = {
                chartDao.getTopTracks(TopListEntryType.USER_TRACKS)
            },
            writer = { _: Any, listings: List<Track> ->
                val topListEntries = TopListMapper.forLists()
                    .invoke(listings.map { Pair(it, TopListEntryType.USER_TRACKS) })
                trackDao.insertEntitiesWithTopListEntries(
                    listings,
                    topListEntries
                )
            }
        )
    ).build().stream(StoreRequest.cached("", true))

    private suspend fun refreshImageUrl(localImageUrl: String?, listing: ListingMin) {
        if (localImageUrl.isNullOrBlank()) {
            println("Refreshing ImageURl for ${listing.name}")
            val url: String? = when (listing) {
                is Artist -> provideSpotifyService(
                    spotifyAuthProvider,
                    spotifyAuthenticator
                ).searchArtist(listing.name).maxBy { item ->
                    item.popularity
                }?.images?.first()?.url
                is Track -> null
                else -> TODO()
            }
            println("ImageUrl $url")
            url?.let {
                listing.imageUrl = url
            }
        }
    }
}