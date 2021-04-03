package de.schnettler.scrobbler.ui.common.compose.viewmodel

import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.fresh
import de.schnettler.scrobbler.ui.common.compose.UiState
import de.schnettler.scrobbler.ui.common.compose.copyWithStoreResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class StoreViewModel<S> : ReduxViewModel<UiState<S>>(UiState(true)) {
    private suspend fun <R> Flow<StoreResponse<R>>.collectIntoState(reducer: (S?, R) -> S) {
        collect { response ->
            setState {
                this.copyWithStoreResponse(response) { s, t ->
                    reducer(s, t)
                }
            }
        }
    }

    fun <Key : Any, Output : Any> Store<Key, Output>.streamIntoState(
        key: Key,
        reducer: (S?, Output) -> S
    ) {
        viewModelScope.launch {
            this@streamIntoState.stream(StoreRequest.cached(key, true)).collectIntoState { state, response ->
                reducer(state, response)
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun <Key : Any, Output : Any> Store<Key, Output>.refreshIntoState(
        key: Key
    ) {
        viewModelScope.launch {
            setState { this.copy(loading = true) }
            try {
                this@refreshIntoState.fresh(key)
            } catch (e: Exception) {
                setState { this.copy(exception = e) }
            }
            setState { this.copy(loading = false) }
        }
    }
}