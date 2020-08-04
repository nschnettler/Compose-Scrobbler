package de.schnettler.repo

import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.models.LastFmEntity
import de.schnettler.lastfm.api.spotify.SpotifyService
import de.schnettler.repo.authentication.AccessTokenAuthenticator
import de.schnettler.repo.authentication.provider.SpotifyAuthProvider
import de.schnettler.repo.util.provideSpotifyService
import timber.log.Timber
import javax.inject.Inject

const val GET_ARTIST_IMAGES_WORK = "get_artist_images"

class ImageRepo @Inject constructor(
    private val artistDao: ArtistDao,
    private val authProvider: SpotifyAuthProvider,
    private val authenticator: AccessTokenAuthenticator,
) {
    suspend fun retrieveMissingArtistImages() {
        val needsImage = artistDao.getIdsOfMissingImages()
        Timber.d("[Spotify] Requesting images for ${needsImage.size} artists")
        val service = provideSpotifyService(authProvider, authenticator)
        needsImage.forEach { artist ->
            retrieveArtistImage(artist = artist, spotifyService = service)
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