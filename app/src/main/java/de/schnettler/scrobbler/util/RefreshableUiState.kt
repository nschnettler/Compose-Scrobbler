package de.schnettler.scrobbler.util

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.onActive
import androidx.compose.setValue
import androidx.compose.state

typealias RepositoryCall<T> = ((Result<T>) -> Unit) -> Unit
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
 * Handler that allows getting the current RefreshableUiState and refresh its content.
 */
data class RefreshableUiStateHandler<out T>(
    val state: RefreshableUiState<T>,
    val refreshAction: () -> Unit
)

/**
 * Refreshable UiState factory that updates its internal state with the
 * [Result] of a callback passed as a parameter.
 *
 * To load asynchronous data, effects are better pattern than using @Model classes since
 * effects are Compose lifecycle aware.
 */
@Composable
fun <T> refreshableUiStateFrom(
    repositoryCall: RepositoryCall<T>
): RefreshableUiStateHandler<T> {

    var state: RefreshableUiState<T> by state<RefreshableUiState<T>> {
        RefreshableUiState.Success(data = null, loading = true)
    }

    val refresh = {
        state = RefreshableUiState.Success(data = state.currentData, loading = true)
        repositoryCall { result ->
            state = when (result) {
                is Result.Success -> RefreshableUiState.Success(
                    data = result.data, loading = false
                )
                is Result.Error -> RefreshableUiState.Error(
                    exception = result.exception, previousData = state.currentData
                )
                is Result.Loading -> RefreshableUiState.Success(
                    data = state.currentData, loading = true
                )
            }
        }
    }

    onActive {
        refresh()
    }

    return RefreshableUiStateHandler(state, refresh)
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
