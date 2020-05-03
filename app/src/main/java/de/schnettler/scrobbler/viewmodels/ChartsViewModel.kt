package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import com.dropbox.android.external.store4.StoreResponse
import com.etiennelenhart.eiffel.viewmodel.StateViewModel
import de.schnettler.repo.Repository
import de.schnettler.scrobbler.util.ChartState
import de.schnettler.scrobbler.util.LoadingStatus
import de.schnettler.scrobbler.util.State
import timber.log.Timber

class ChartsViewModel(val repo: Repository) : StateViewModel<ChartState>() {
    override val state = MediatorLiveData<ChartState>()

    private val artistResponse by lazy {
        Timber.d("Loading Artists")
        repo.getTopArtists().asLiveData()
    }

    val test: LiveData<ChartState>
        get() = state

    init {
        initState { ChartState(State(listOf(), LoadingStatus.Init)) }
        stateInitialized

        state.addSource(artistResponse) {response ->
            when (response) {
                is StoreResponse.Data -> updateState { state ->
                    state.copy(artistState = state.artistState.copy(data = response.value)) }
                is StoreResponse.Loading -> updateState { state ->
                    state.copy(artistState = state.artistState.copy(status = LoadingStatus.Loading)) }
                is StoreResponse.Error -> updateState { state ->
                    state.copy(artistState = state.artistState.copy(status = LoadingStatus.Error(response.errorMessageOrNull()))) }
            }
        }
    }
}