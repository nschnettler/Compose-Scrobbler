package de.schnettler.scrobbler.util

/**
 * Model for UiStates that can refresh. The Success state contains whether there's data loading
 * apart from the current data to display on the screen. The error state also returns previously
 * loaded data apart from the error.
 */
sealed class RefreshableUiState<out T> {
    data class Success<out T>(val data: T?, val loading: Boolean) : RefreshableUiState<T>()
    data class Error<out T>(val exception: Exception, val previousData: T?) :
        RefreshableUiState<T>()
}

/**
 * A generic class that holds a value or an exception
 */
sealed class Result<out R> {
    object Loading: Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

val <T> RefreshableUiState<T>.loading: Boolean
    get() = this is RefreshableUiState.Success && this.loading && this.data == null

val <T> RefreshableUiState<T>.refreshing: Boolean
    get() = this is RefreshableUiState.Success && this.loading && this.data != null

val <T> RefreshableUiState<T>.currentData: T?
    get() = when (this) {
        is RefreshableUiState.Success -> this.data
        is RefreshableUiState.Error -> this.previousData
    }

val <T> RefreshableUiState<T>.error: Exception?
    get() = if(this is RefreshableUiState.Error) {
        this.exception
    } else {
        null
    }
