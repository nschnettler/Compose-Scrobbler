package de.schnettler.lastfm.map

import de.schnettler.lastfm.models.ErrorResponse
import de.schnettler.lastfm.models.LastFmResponse
import de.schnettler.lastfm.parseErrJsonResponse
import retrofit2.Response

object ResponseToLastFmResponseMapper {
    fun <T> map(from: Response<T>): LastFmResponse<T> = with(from) {
        if (isSuccessful) {
            LastFmResponse.SUCCESS(body())
        } else {
            LastFmResponse.ERROR(parseErrJsonResponse<ErrorResponse>()?.asError())
        }
    }
}