package de.schnettler.repo.util

import de.schnettler.lastfm.api.provideAuthenticatedSpotifyService
import de.schnettler.repo.authentication.AccessTokenAuthenticator
import de.schnettler.repo.authentication.provider.SpotifyAuthProvider
import de.schnettler.repo.mapping.response.LastFmResponse

suspend fun provideSpotifyService(
    authProvider: SpotifyAuthProvider,
    authenticator: AccessTokenAuthenticator
) = provideAuthenticatedSpotifyService(
    authProvider.getToken().token,
    authenticator = authenticator
)

fun Long.toBoolean() = this == 1L

@Suppress("TooGenericExceptionCaught")
inline fun <T> safePost(post: () -> LastFmResponse<T>): LastFmResponse<T> =
    try {
        post()
    } catch (ex: Exception) {
        LastFmResponse.EXCEPTION(ex)
    }