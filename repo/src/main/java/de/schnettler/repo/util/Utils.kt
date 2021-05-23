package de.schnettler.repo.util

import de.schnettler.repo.mapping.response.LastFmResponse

fun Long.toBoolean() = this == 1L

@Suppress("TooGenericExceptionCaught")
inline fun <T> safePost(post: () -> LastFmResponse<T>): LastFmResponse<T> =
    try {
        post()
    } catch (ex: Exception) {
        LastFmResponse.EXCEPTION(ex)
    }