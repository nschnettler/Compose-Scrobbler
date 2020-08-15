package de.schnettler.lastfm

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import retrofit2.Response

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