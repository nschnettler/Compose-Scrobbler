package de.schnettler.scrobbler.util

/**
 * Model for UiStates that can refresh. The Success state contains whether there's data loading
 * apart from the current data to display on the screen. The error state also returns previously
 * loaded data apart from the error.
 */
sealed class RefreshableUiState<out T> {
    data class Success<out T>(
        val data: T?,
        val loading: Boolean
    ) : RefreshableUiState<T>()

    data class Error<out T>(
        val exception: Throwable? = null,
        val errorMessage: String? = null,
        val previousData: T?
    ) : RefreshableUiState<T>()

    val isLoading: Boolean
        get() = this is Success && this.loading && this.data == null

    val isRefreshing: Boolean
        get() = this is Success && this.loading && this.data != null

    val currentData: T?
        get() = when (this) {
            is Success -> this.data
            is Error -> this.previousData
        }
}