package de.schnettler.repo.mapping

import de.schnettler.lastfm.models.ErrorResponse
import de.schnettler.lastfm.models.Errors
import de.schnettler.lastfm.parseErrJsonResponse
import retrofit2.Response

fun <T: Any> Response<T>.map(): LastFmPostResponse<T> =
        if (isSuccessful) {
            LastFmPostResponse.SUCCESS(body())
        } else {
            LastFmPostResponse.ERROR(parseErrJsonResponse<ErrorResponse>()?.asError())
        }

sealed class LastFmPostResponse<out T : Any?> {
    class SUCCESS<out T : Any>(val data: T?): LastFmPostResponse<T>()
    class ERROR(val error: Errors?): LastFmPostResponse<Nothing>()
}