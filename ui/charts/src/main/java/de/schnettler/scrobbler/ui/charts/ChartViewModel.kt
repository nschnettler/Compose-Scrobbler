package de.schnettler.scrobbler.ui.charts

import androidx.lifecycle.ViewModel
import de.schnettler.scrobbler.core.ui.state.RefreshableUiState
import de.schnettler.scrobbler.model.TopListArtist
import de.schnettler.scrobbler.model.TopListTrack
import kotlinx.coroutines.flow.StateFlow

abstract class ChartViewModel : ViewModel() {
    abstract val artistState: StateFlow<RefreshableUiState<List<TopListArtist>>>
    abstract val trackState: StateFlow<RefreshableUiState<List<TopListTrack>>>

    abstract fun refresh(tab: ChartTab)
}