package de.schnettler.lastfm

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import de.schnettler.lastfm.map.ResponseToLastFmResponseMapper
import de.schnettler.lastfm.models.LastFmResponse
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

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

fun extractErrorMessageFromException(exception: Throwable): String? {
    return when (exception) {
        is HttpException -> {
            val response = exception.response() ?: return null
            (ResponseToLastFmResponseMapper.map(response) as? LastFmResponse.ERROR)?.error?.title
        }
        is UnknownHostException -> {
            "Network unavailable"
        }
        else -> null
    }
}