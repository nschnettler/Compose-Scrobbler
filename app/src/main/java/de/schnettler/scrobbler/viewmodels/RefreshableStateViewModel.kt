package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.Store
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.scrobbler.util.freshFrom
import de.schnettler.scrobbler.util.streamFrom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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
        viewModelScope.launch(Dispatchers.IO) {
            state.freshFrom(store, key)
        }
    }

    /**
     * Starts to stream data, if not streaming yet.
     */
    fun startStream() {
        if (!streaming) {
            streaming = true
            viewModelScope.launch(Dispatchers.IO) {
                state.streamFrom(store, key)
            }
        }
    }
}