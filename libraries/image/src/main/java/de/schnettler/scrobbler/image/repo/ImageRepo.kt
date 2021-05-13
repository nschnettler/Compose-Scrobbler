package de.schnettler.scrobbler.image.repo

import de.schnettler.scrobbler.image.api.SpotifyApi
import de.schnettler.scrobbler.image.db.ImageDao
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.persistence.dao.AlbumDao
import timber.log.Timber
import javax.inject.Inject

const val GET_ARTIST_IMAGES_WORK = "get_artist_images"

class ImageRepo @Inject constructor(
    private val imageDao: ImageDao,
    private val albumDao: AlbumDao,
    private val spotifyApi: SpotifyApi
) {
    suspend fun retrieveMissingArtistImages() {
        val needsImage = imageDao.getTopArtistsWithoutImages()
        Timber.d("[Spotify] Requesting images for ${needsImage.size} artists")
        needsImage.forEach { artist ->
            updateArtistImage(artist)
        }

        val trackNeedsImage = imageDao.getTopTracksWithoutImages()
        Timber.d("[Spotify] Requesting images for ${trackNeedsImage.size} tracks")
        trackNeedsImage.forEach { track ->
            track.album?.let { albumName ->
                val album = albumDao.getAlbumByName(name = albumName, artist = track.artist)
                album?.imageUrl?.let { image ->
                    imageDao.updateTrackImageUrl(track.id, image)
                }
            }
        }
    }

    suspend fun updateArtistImage(
        artist: LastFmEntity.Artist,
        maxRes: Long = 1000,
    ) {
        val image = spotifyApi.searchArtist(artist.name).maxByOrNull {
            it.popularity
        }?.images?.firstOrNull {
            it.height < maxRes
        }
        Timber.d("[Work] Selected for ${artist.name}: $image")
        image?.url?.let { imageDao.updateArtistImageUrl(artist.id, it) }
    }
}