package de.schnettler.lastfm

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import de.schnettler.lastfm.api.lastfm.LastFmService
import retrofit2.Response
import java.security.MessageDigest

inline fun <reified T> Response<*>.parseErrJsonResponse(): T? {
    val moshi = Moshi.Builder()/*.add(KotlinJsonAdapterFactory())*/.build()
    val parser = moshi.adapter(T::class.java)
    val response = errorBody()?.string()
    if (!response.isNullOrBlank()) {
        try {
            return parser.fromJson(response)
        } catch (e: JsonDataException) {
            e.printStackTrace()
        }
    }
    return null
}

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

fun String.md5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.joinToString("") {
        "%02x".format(it)
    }
}