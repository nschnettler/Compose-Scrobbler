package de.schnettler.scrobbler.image.repo

import de.schnettler.scrobbler.image.api.SpotifyApi
import de.schnettler.scrobbler.image.db.ImageDao
import de.schnettler.scrobbler.image.model.SpotifyArtist
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.persistence.dao.AlbumDao
import de.schnettler.scrobbler.persistence.dao.ArtistDao
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val GET_ARTIST_IMAGES_WORK = "get_artist_images"

class ImageRepo @Inject constructor(
    private val imageDao: ImageDao,
    private val albumDao: AlbumDao,
    private val spotifyApi: SpotifyApi,
    private val artistDao: ArtistDao,
) {
    suspend fun retrieveMissingArtistImages() {
        val maxTimestamp = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)

        val needsImage = imageDao.getOutdatedTopArtists(maxTimestamp)
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
        val spotifyId = artist.spotifyId

        val spotifyArtist = if (!spotifyId.isNullOrEmpty()) {
            fetchSpotifyArtist(spotifyId)
        } else {
            searchSpotifyArtist(artist.id, artist.name)
        }

        val image = spotifyArtist?.images?.firstOrNull { it.height < maxRes }

        Timber.d("[Work] Selected for ${artist.name}: $image")

        image?.url?.let { imageDao.updateArtistImage(artist.id, it, System.currentTimeMillis()) }
    }

    private suspend fun fetchSpotifyArtist(artistSpotifyId: String): SpotifyArtist? {
        return spotifyApi.getArtist(artistSpotifyId)
    }

    private suspend fun searchSpotifyArtist(
        artistId: String,
        artistName: String
    ): SpotifyArtist? {
        val result = spotifyApi.searchArtist(artistName)

        val matchingArtists = result.filter { it.name.equals(artistName, true) }

        Timber.d("[Work] Found ${matchingArtists.size} matching artists for $artistName")

        val matchingArtist = matchingArtists.sortedByDescending { it.popularity }.firstOrNull { it.images.isNotEmpty() }

        if (matchingArtist != null) {
            artistDao.setSpotifyId(artistId, matchingArtist.id)
        }

        return matchingArtist
    }
}