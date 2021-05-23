package de.schnettler.scrobbler.core.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.fresh
import de.schnettler.scrobbler.core.ui.state.UiState
import de.schnettler.scrobbler.core.ui.state.copyWithStoreResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class StoreViewModel<S> : ReduxViewModel<UiState<S>>(UiState(true)) {
    private suspend fun <R> Flow<StoreResponse<R>>.collectIntoState(reducer: (S?, R) -> S) {
        collect { response ->
            setState {
                this.copyWithStoreResponse(response, reducer)
            }
        }
    }

    fun <Key : Any, Output : Any> Store<Key, Output>.streamIntoState(
        key: Key,
        reducer: (S?, Output) -> S
    ) {
        viewModelScope.launch {
            this@streamIntoState.stream(StoreRequest.cached(key, true)).collectIntoState(reducer)
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
                Timber.e(e)
                setState { this.copy(exception = e) }
            }
            setState { this.copy(loading = false) }
        }
    }
}