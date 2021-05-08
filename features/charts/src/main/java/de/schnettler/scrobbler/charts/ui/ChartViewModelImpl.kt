package de.schnettler.scrobbler.charts.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.scrobbler.charts.model.ChartTab
import de.schnettler.scrobbler.charts.repo.ChartRepository
import de.schnettler.scrobbler.core.ktx.freshFrom
import de.schnettler.scrobbler.core.ktx.streamFrom
import de.schnettler.scrobbler.core.ui.state.RefreshableUiState
import de.schnettler.scrobbler.model.TopListArtist
import de.schnettler.scrobbler.model.TopListTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartViewModelImpl @Inject constructor(private val repo: ChartRepository) : ChartViewModel() {
    override val artistState: MutableStateFlow<RefreshableUiState<List<TopListArtist>>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))

    override val trackState: MutableStateFlow<RefreshableUiState<List<TopListTrack>>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))

    init {
        viewModelScope.apply {
            launch { artistState.streamFrom(repo.chartArtistsStore, "") }
            launch { trackState.streamFrom(repo.chartTrackStore, "") }
        }
    }

    override fun refresh(tab: ChartTab) {
        viewModelScope.launch {
            when (tab) {
                ChartTab.Track -> trackState.freshFrom(repo.chartTrackStore, "")
                ChartTab.Artist -> artistState.freshFrom(repo.chartArtistsStore, "")
            }
        }
    }
}