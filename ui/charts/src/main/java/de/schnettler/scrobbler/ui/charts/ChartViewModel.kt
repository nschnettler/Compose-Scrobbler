package de.schnettler.scrobbler.ui.charts

import androidx.lifecycle.ViewModel
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListTrack
import de.schnettler.scrobbler.ui.common.compose.RefreshableUiState
import kotlinx.coroutines.flow.StateFlow

abstract class ChartViewModel : ViewModel() {
    abstract val artistState: StateFlow<RefreshableUiState<List<TopListArtist>>>
    abstract val trackState: StateFlow<RefreshableUiState<List<TopListTrack>>>

    abstract fun refresh(tab: ChartTab)
}