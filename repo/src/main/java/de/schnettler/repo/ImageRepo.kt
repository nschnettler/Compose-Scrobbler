package de.schnettler.repo

import de.schnettler.database.daos.AlbumDao
import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.daos.ChartDao
import de.schnettler.database.daos.TrackDao
import de.schnettler.database.models.LastFmEntity
import de.schnettler.lastfm.api.spotify.SpotifyService
import de.schnettler.repo.authentication.AccessTokenAuthenticator
import de.schnettler.repo.authentication.provider.SpotifyAuthProvider
import de.schnettler.repo.util.provideSpotifyService
import timber.log.Timber
import javax.inject.Inject

const val GET_ARTIST_IMAGES_WORK = "get_artist_images"

class ImageRepo @Inject constructor(
    private val chartDao: ChartDao,
    private val artistDao: ArtistDao,
    private val trackDao: TrackDao,
    private val authProvider: SpotifyAuthProvider,
    private val authenticator: AccessTokenAuthenticator,
    private val albumDao: AlbumDao
) {
    suspend fun retrieveMissingArtistImages() {
        val needsImage = chartDao.getArtistsWithoutImages()
        Timber.d("[Spotify] Requesting images for ${needsImage.size} artists")
        val service = provideSpotifyService(authProvider, authenticator)
        needsImage.forEach { artist ->
            retrieveArtistImage(artist = artist, spotifyService = service)
        }

        val trackNeedsImage = chartDao.getTracksWithoutImages()
        Timber.d("[Spotify] Requesting images for ${trackNeedsImage.size} tracks")
        trackNeedsImage.forEach { track ->
            track.album?.let { albumName ->
                val album = albumDao.getAlbumByName(name = albumName, artist = track.artist)
                album?.imageUrl?.let {image ->
                    trackDao.updateImageUrl(track.id, image)
                }
            }
        }
    }

    suspend fun retrieveArtistImage(
        artist: LastFmEntity.Artist,
        maxRes: Long = 1000,
        spotifyService: SpotifyService? = null
    ) {
        val service = spotifyService ?: provideSpotifyService(authProvider, authenticator)
        val image = service.searchArtist(artist.name)
            .maxByOrNull { it.popularity }?.images?.firstOrNull { it.height < maxRes }
        Timber.d("[Work] Selected for ${artist.name}: $image")
        image?.url?.let { artistDao.updateArtistImageUrl(artist.id, it) }
    }
}