package de.schnettler.repo.util

import de.schnettler.lastfm.api.RetrofitService
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.authentication.AccessTokenAuthenticator
import de.schnettler.repo.authentication.provider.SpotifyAuthProvider
import timber.log.Timber
import java.net.URLEncoder
import java.security.MessageDigest

//fun String.md5(): String {
//    val md = MessageDigest.getInstance("MD5")
//    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
//}

fun createSignature(params: MutableMap<String, String>): String {
    params["api_key"] = LastFmService.API_KEY
    val sorted = params.toSortedMap()
    val signature = StringBuilder()
    sorted.forEach { (key, value) ->
        signature.append(key)
        signature.append(value)
    }
    signature.append(LastFmService.SECRET)
    return signature.toString().md5()
}

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

fun Long.toBoolean() = this == 1L


suspend fun provideSpotifyService(
    authProvider: SpotifyAuthProvider,
    authenticator: AccessTokenAuthenticator
) =
    RetrofitService.provideAuthenticatedSpotifyService(
        authProvider.getToken().token,
        authenticator = authenticator
    )

fun String.md5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.joinToString("") {
        "%02x".format(it)
    }
}