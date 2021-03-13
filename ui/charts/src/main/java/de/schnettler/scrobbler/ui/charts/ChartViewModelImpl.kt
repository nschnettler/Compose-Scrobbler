package de.schnettler.scrobbler.ui.charts

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListTrack
import de.schnettler.repo.ChartRepository
import de.schnettler.scrobbler.ui.common.compose.RefreshableUiState
import de.schnettler.scrobbler.ui.common.compose.freshFrom
import de.schnettler.scrobbler.ui.common.compose.streamFrom
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