package de.schnettler.repo.mapping

import de.schnettler.lastfm.models.ErrorResponse
import de.schnettler.lastfm.models.Errors
import de.schnettler.lastfm.parseErrJsonResponse
import retrofit2.Response

fun <T : Any> Response<T>.map(): LastFmResponse<T> =
    if (isSuccessful) {
        LastFmResponse.SUCCESS(body())
    } else {
        LastFmResponse.ERROR(parseErrJsonResponse<ErrorResponse>()?.asError())
    }

sealed class LastFmResponse<out T : Any?> {
    class SUCCESS<out T : Any>(val data: T?) : LastFmResponse<T>()
    class ERROR(val error: Errors?) : LastFmResponse<Nothing>()
}