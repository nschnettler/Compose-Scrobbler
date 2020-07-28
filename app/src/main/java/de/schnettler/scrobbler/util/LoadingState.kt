package de.schnettler.scrobbler.util

import android.content.Context
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import de.schnettler.repo.Result
import de.schnettler.repo.mapping.LastFmResponse
import de.schnettler.repo.mapping.map
import retrofit2.HttpException
import java.net.UnknownHostException

sealed class LoadingState<T>(open val data: T? = null) {
    class Initial<T> : LoadingState<T>()
    data class Loading<T>(
        val origin: ResponseOrigin,
        val oldData: T? = null
    ) : LoadingState<T>(oldData)

    data class Data<T>(
        val origin: ResponseOrigin,
        val newData: T
    ) : LoadingState<T>(newData)

    data class Error<T>(
        val errorMsg: String?,
        val exception: Throwable? = null,
        val oldData: T? = null
    ) : LoadingState<T>(oldData)

    fun handleIfError(ctx: Context) {
        if (this is Error) {
            errorMsg?.let { ctx.toast(it) }
            exception?.let { Timber.e(it) }
        }
    }
}

fun <T> MutableStateFlow<LoadingState<T>>.updateState(response: StoreResponse<T>) {
    value = when (response) {
        is StoreResponse.Data -> LoadingState.Data(
            origin = response.origin,
            newData = response.value
        )
        is StoreResponse.Loading -> LoadingState.Loading(
            origin = response.origin,
            oldData = value.data
        )
        is StoreResponse.Error.Message -> LoadingState.Error(
            errorMsg = response.errorMessageOrNull(),
            oldData = value.data
        )
        is StoreResponse.Error.Exception -> LoadingState.Error(
            errorMsg = response.errorMessageOrNull(),
            exception = response.error,
            oldData = value.data
        )
    }
}

fun <T> MutableStateFlow<RefreshableUiState<T>>.update(result: Result<T>) {
    value = when (result) {
        is Result.Success -> RefreshableUiState.Success(
            data = result.data, loading = false
        )
        is Result.Error -> {
            RefreshableUiState.Error(
                exception = result.exception,
                previousData = this.value.currentData,
                errorMessage = extractErrorMessageFromException(result.exception)
            )
        }
        is Result.Loading -> RefreshableUiState.Success(
            data = this.value.currentData, loading = true
        )
    }
}

fun <T> MutableStateFlow<RefreshableUiState<T>>.update(result: StoreResponse<T>) {
    value = when (result) {
        is StoreResponse.Data -> RefreshableUiState.Success(
            data = result.value, loading = false
        )
        is StoreResponse.Error.Exception -> RefreshableUiState.Error(
            exception = result.error,
            previousData = this.value.currentData,
            errorMessage = extractErrorMessageFromException(result.error)
        ).also {
            Timber.e(it.exception)
        }
        is StoreResponse.Error.Message -> RefreshableUiState.Error(
            errorMessage = result.message, previousData = this.value.currentData
        )
        is StoreResponse.Loading -> RefreshableUiState.Success(
            data = this.value.currentData, loading = true
        )
    }
}

private fun extractErrorMessageFromException(exception: Throwable): String? {
    return when (exception) {
        is HttpException -> {
            (exception.response()?.map() as? LastFmResponse.ERROR)?.error?.title
        }
        is UnknownHostException -> {
            "Network unavailable"
        }
        else -> null
    }
}