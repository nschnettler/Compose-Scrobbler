package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.Store
import de.schnettler.scrobbler.ui.common.compose.RefreshableUiState
import de.schnettler.scrobbler.ui.common.compose.freshFrom
import de.schnettler.scrobbler.ui.common.compose.streamFrom
import de.schnettler.scrobbler.ui.common.compose.updateValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

open class RefreshableStateViewModel<Key : Any, StateType : Any, Output : StateType>(
    private val store: Store<Key, Output>,
    private val key: Key
) : ViewModel() {
    val state: MutableStateFlow<RefreshableUiState<StateType>> by lazy {
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))
    }

    private var streaming: Boolean = false

    /**
     * Refreshes Data
     */
    fun refresh() {
        viewModelScope.launch {
            state.freshFrom(store, key)
        }
    }

    /**
     * Starts to stream data, if not streaming yet.
     */
    fun startStream() {
        if (!streaming) {
            streaming = true
            viewModelScope.launch {
                state.streamFrom(store, key)
            }
        }
    }
}

open class RefreshableStateViewModel2<Key : Any, StateType : Any, Output : StateType>(
    private val store: Store<Key, Output>,
) : ViewModel() {
    private val keyState: MutableStateFlow<Key?> = MutableStateFlow(null)

    val state: MutableStateFlow<RefreshableUiState<StateType>> by lazy {
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))
    }

    init {
        viewModelScope.launch {
            keyState.filterNotNull().collectLatest { key ->
                state.streamFrom(store, key)
            }
        }
    }

    /**
     * Refreshes Data
     */
    fun refresh() {
        keyState.value?.let { key ->
            viewModelScope.launch {
                state.freshFrom(store, key)
            }
        }
    }

    fun updateKey(new: Key) {
        if (keyState.updateValue(new)) {
            state.value = RefreshableUiState.Success(data = null, loading = true)
        }
    }
}