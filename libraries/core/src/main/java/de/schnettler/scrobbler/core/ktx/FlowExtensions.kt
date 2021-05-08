package de.schnettler.scrobbler.core.ktx

import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.fresh
import de.schnettler.scrobbler.core.ui.state.RefreshableUiState
import de.schnettler.scrobbler.model.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Suppress("TooGenericExceptionCaught")
suspend fun <Key : Any, StateType : Any, Output : StateType> MutableStateFlow<RefreshableUiState<StateType>>.freshFrom(
    store: Store<Key, Output>,
    key: Key
) = refreshStateFlowFromStore(this, store, key)

suspend fun <Key : Any, StateType : Any, Output : StateType> MutableStateFlow<RefreshableUiState<StateType>>.streamFrom(
    store: Store<Key, Output>,
    key: Key
) = store.stream(StoreRequest.cached(key, true)).collectLatest { update(it) }

@Suppress("TooGenericExceptionCaught")
suspend inline fun <Key : Any, StateType : Any, Output : StateType> refreshStateFlowFromStore(
    flow: MutableStateFlow<RefreshableUiState<StateType>>?,
    store: Store<Key, Output>,
    key: Key
) {
    flow?.update(Result.Loading)
    try {
        store.fresh(key)
    } catch (e: Exception) {
        Timber.e(e)
        flow?.update(Result.Error(e))
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
//                errorMessage = extractErrorMessageFromException(result.exception) // TODO: Find better way
            ).also {
                Timber.e(result.exception)
            }
        }
        is Result.Loading -> RefreshableUiState.Success(
            data = this.value.currentData, loading = true
        )
    }
}

fun <T> MutableStateFlow<T>.updateValue(newValue: T): Boolean {
    if (value != newValue) {
        value = newValue
        return true
    }
    return false
}

fun <T> MutableStateFlow<RefreshableUiState<T>>.update(result: StoreResponse<T>) {
    value = when (result) {
        is StoreResponse.Data -> RefreshableUiState.Success(
            data = result.value, loading = false
        )
        is StoreResponse.Error.Exception -> RefreshableUiState.Error(
            exception = result.error,
            previousData = this.value.currentData,
//            errorMessage = extractErrorMessageFromException(result.error)
        ).also {
            Timber.e(it.exception)
        }
        is StoreResponse.Error.Message -> RefreshableUiState.Error(
            errorMessage = result.message, previousData = this.value.currentData
        ).also {
            Timber.d(result.message)
        }
        is StoreResponse.Loading -> RefreshableUiState.Success(
            data = this.value.currentData, loading = true
        )
        is StoreResponse.NoNewData -> RefreshableUiState.Success(
            data = this.value.currentData, loading = false
        )
    }
}