package de.schnettler.scrobbler.ui.charts

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListTrack
import de.schnettler.repo.ChartRepository
import de.schnettler.scrobbler.ui.common.compose.RefreshableUiState
import de.schnettler.scrobbler.ui.common.compose.freshFrom
import de.schnettler.scrobbler.ui.common.compose.streamFrom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChartsViewModel @ViewModelInject constructor(private val repo: ChartRepository) : ViewModel() {
    private val _artistState: MutableStateFlow<RefreshableUiState<List<TopListArtist>>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))
    val artistState: StateFlow<RefreshableUiState<List<TopListArtist>>>
        get() = _artistState

    private val _trackState: MutableStateFlow<RefreshableUiState<List<TopListTrack>>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))
    val trackState: StateFlow<RefreshableUiState<List<TopListTrack>>>
        get() = _trackState

    init {
        viewModelScope.apply {
            launch { _artistState.streamFrom(repo.chartArtistsStore, "") }
            launch { _trackState.streamFrom(repo.chartTrackStore, "") }
        }
    }

    fun refresh(tab: ChartTab) {
        viewModelScope.launch {
            when (tab) {
                ChartTab.Track -> _trackState.freshFrom(repo.chartTrackStore, "")
                ChartTab.Artist -> _artistState.freshFrom(repo.chartArtistsStore, "")
            }
        }
    }
}