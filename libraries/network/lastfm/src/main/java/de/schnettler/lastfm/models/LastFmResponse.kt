package de.schnettler.lastfm.models

sealed class LastFmResponse<out T : Any?> {
    class SUCCESS<out T : Any>(val data: T?) : LastFmResponse<T>()
    class ERROR(val error: Errors?) : LastFmResponse<Nothing>()
    class EXCEPTION(val exception: Throwable) : LastFmResponse<Nothing>()
}