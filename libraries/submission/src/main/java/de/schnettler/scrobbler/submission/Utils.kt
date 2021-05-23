package de.schnettler.scrobbler.submission

import de.schnettler.lastfm.models.LastFmResponse

@Suppress("TooGenericExceptionCaught")
inline fun <T> safePost(post: () -> LastFmResponse<T>): LastFmResponse<T> =
    try {
        post()
    } catch (ex: Exception) {
        LastFmResponse.EXCEPTION(ex)
    }