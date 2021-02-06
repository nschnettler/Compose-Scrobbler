package de.schnettler.repo.util

import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.lastfm.api.provideAuthenticatedSpotifyService
import de.schnettler.repo.authentication.AccessTokenAuthenticator
import de.schnettler.repo.authentication.provider.SpotifyAuthProvider
import de.schnettler.repo.mapping.response.LastFmResponse
import java.net.URLEncoder

fun createBody(params: MutableMap<String, String>): String {
    params["api_key"] = LastFmService.API_KEY
    val sorted = params.toSortedMap()
    val signature = StringBuilder()
    sorted.forEach { (key, value) ->
        if (signature.isNotEmpty()) signature.append('&')
        signature.append(key)
        signature.append("=")
        signature.append(URLEncoder.encode(value, "UTF-8"))
    }
    return signature.toString()
}

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