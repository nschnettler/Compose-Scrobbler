package de.schnettler.scrobbler.core.model

/**
 * A generic class that holds a value or an exception
 */
sealed class Result<out R> {
    object Loading : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}