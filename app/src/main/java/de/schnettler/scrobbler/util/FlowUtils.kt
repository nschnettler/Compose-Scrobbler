package de.schnettler.scrobbler.util

import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.fresh
import de.schnettler.repo.Result
import de.schnettler.repo.mapping.LastFmResponse
import de.schnettler.repo.mapping.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import retrofit2.HttpException
import timber.log.Timber
import java.net.UnknownHostException

@Suppress("TooGenericExceptionCaught")
suspend fun <K : Any, V : Any> MutableStateFlow<RefreshableUiState<V>>.freshFrom(
    store: Store<K, V>,
    key: K
) = refreshStateFlowFromStore(this, store, key)

suspend fun <K : Any, V : Any> MutableStateFlow<RefreshableUiState<V>>.streamFrom(
    store: Store<K, V>,
    key: K
) = store.stream(StoreRequest.cached(key, true)).collectLatest { update(it) }

@Suppress("TooGenericExceptionCaught")
suspend inline fun <K : Any, V : Any> refreshStateFlowFromStore(
    flow: MutableStateFlow<RefreshableUiState<V>>?,
    store: Store<K, V>,
    key: K
) {
    flow?.update(Result.Loading)
    try {
        store.fresh(key)
    } catch (e: Exception) {
        flow?.update(Result.Error(e))
    }
}

fun <T> MutableStateFlow<T>.updateValue(newValue: T): Boolean {
    if (value != newValue) {
        value = newValue
        return true
    }
    return false
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