package de.schnettler.repo.mapping

import de.schnettler.lastfm.models.ErrorResponse
import de.schnettler.lastfm.models.Errors
import de.schnettler.lastfm.parseErrJsonResponse
import retrofit2.Response

fun <T> Response<T>.map(): LastFmPostResponse =
        if (isSuccessful) {
            LastFmPostResponse.SUCCESS(body())
        } else {
            LastFmPostResponse.ERROR(parseErrJsonResponse<ErrorResponse>()?.asError())
        }

sealed class LastFmPostResponse {
    class SUCCESS<T>(val data: T): LastFmPostResponse()
    class ERROR(val error: Errors?): LastFmPostResponse()
}