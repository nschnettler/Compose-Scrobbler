package de.schnettler.repo.util

import de.schnettler.lastfm.api.lastfm.LastFmService
import java.math.BigInteger
import java.security.MessageDigest

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

fun createSignature(method: String, params: MutableMap<String, String>, secret: String): String {
    params["method"] = method
    params["api_key"] = LastFmService.API_KEY
    val sorted = params.toSortedMap()
    val signature = StringBuilder()
    sorted.forEach { (key, value) ->
        signature.append(key)
        signature.append(value)
    }
    signature.append(secret)
    return signature.toString().md5()
}

fun Long.toBoolean() = this == 1L