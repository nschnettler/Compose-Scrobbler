package de.schnettler.lastfm

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import retrofit2.Response
import java.security.MessageDigest

inline fun <reified T> Response<*>.parseErrJsonResponse(): T? {
    val moshi = Moshi.Builder().build()
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

fun String.md5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.joinToString("") {
        "%02x".format(it)
    }
}