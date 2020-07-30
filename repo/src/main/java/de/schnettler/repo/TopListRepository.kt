package de.schnettler.repo

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.common.TimePeriod
import de.schnettler.database.daos.AlbumDao
import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.daos.ChartDao
import de.schnettler.database.daos.TrackDao
import de.schnettler.database.daos.UserDao
import de.schnettler.database.models.Album
import de.schnettler.database.models.Artist
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.TopListEntryType
import de.schnettler.database.models.Track
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.authentication.AccessTokenAuthenticator
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.authentication.provider.SpotifyAuthProvider
import de.schnettler.repo.mapping.TopListMapper
import de.schnettler.repo.mapping.UserAlbumMapper
import de.schnettler.repo.mapping.UserArtistMapper
import de.schnettler.repo.mapping.UserTrackMapper
import de.schnettler.repo.mapping.forLists
import de.schnettler.repo.util.provideSpotifyService
import java.util.Locale
import javax.inject.Inject

class TopListRepository @Inject constructor(
    private val userDao: UserDao,
    private val artistDao: ArtistDao,
    private val albumDao: AlbumDao,
    private val trackDao: TrackDao,
    private val chartDao: ChartDao,
    private val service: LastFmService,
    private val albumMapper: UserAlbumMapper,
    private val artistMapper: UserArtistMapper,
    private val trackMapper: UserTrackMapper,
    private val authProvider: LastFmAuthProvider,
    private val spotifyAuthProvider: SpotifyAuthProvider,
    private val spotifyAuthenticator: AccessTokenAuthenticator
) {
    val topArtistStore = StoreBuilder.from(
        fetcher = Fetcher.of { timePeriod: TimePeriod ->
            val session = authProvider.getSessionOrThrow()
            val response = service.getUserTopArtists(timePeriod, session.key)
            val artists = artistMapper.forLists()(response.artist)
            if (timePeriod == TimePeriod.OVERALL) {
                userDao.updateArtistCount(session.name, response.info.total)
            }
            artists.forEach { artist ->
                refreshImageUrl(artistDao.getArtistImageUrl(artist.id), artist)
                artist.imageUrl?.let { artistDao.updateArtistImageUrl(it, artist.id) }
            }
            artists
        },
        sourceOfTruth = SourceOfTruth.of(
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
    ).build()

    val topAlbumStore = StoreBuilder.from(
        fetcher = Fetcher.of { timePeriod: TimePeriod ->
            val albums = service.getUserTopAlbums(timePeriod, authProvider.getSessionKeyOrThrow())
            albumMapper.forLists()(albums)
        },
        sourceOfTruth = SourceOfTruth.of(
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
    ).build()

    val topTracksStore = StoreBuilder.from(
        fetcher = Fetcher.of { timePeriod: TimePeriod ->
            val tracks = service.getUserTopTracks(timePeriod, authProvider.getSessionKeyOrThrow())
            trackMapper.forLists()(tracks)
        },
        sourceOfTruth = SourceOfTruth.of(
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
                listings.forEach { track ->
                    val loaded = trackDao.getSingletTrack(track.id, track.artist)
                    if (loaded?.imageUrl == null) {
                        loaded?.album?.let { album ->
                            val url = albumDao.getImageUrl(album.toLowerCase(Locale.US))
                            url?.let { trackDao.updateImageUrl(it, track.id) }
                        }
                    }
                }
            }
        )
    ).build()

    private suspend fun refreshImageUrl(localImageUrl: String?, listing: LastFmEntity) {
        if (localImageUrl.isNullOrBlank()) {
            println("Refreshing ImageURl for ${listing.name}")
            val url: String? = when (listing) {
                is Artist -> provideSpotifyService(
                                spotifyAuthProvider,
                                spotifyAuthenticator
                            ).searchArtist(listing.name).maxByOrNull { item ->
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