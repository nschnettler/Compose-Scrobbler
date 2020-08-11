package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.database.models.TopListArtist
import de.schnettler.repo.ChartRepository
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.scrobbler.util.freshFrom
import de.schnettler.scrobbler.util.streamFrom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ChartsViewModel @ViewModelInject constructor(
    private val repo: ChartRepository
) : ViewModel() {
    val chartState: MutableStateFlow<RefreshableUiState<List<TopListArtist>>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))

    init {
        viewModelScope.launch(Dispatchers.IO) {
            chartState.streamFrom(repo.chartArtistsStore, "")
        }
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            chartState.freshFrom(repo.chartArtistsStore, "")
        }
    }
}