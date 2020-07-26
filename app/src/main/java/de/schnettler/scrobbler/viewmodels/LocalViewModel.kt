package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.database.models.LocalTrack
import de.schnettler.repo.LocalRepository
import de.schnettler.repo.Result
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.scrobbler.util.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LocalViewModel @ViewModelInject constructor(
    private val repo: LocalRepository
) : ViewModel() {

    val recentTracksState: MutableStateFlow<RefreshableUiState<List<LocalTrack>>> =
            MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getRecentTracks().collect {
                recentTracksState.update(it)
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun refresh() {
        recentTracksState.update(Result.Loading)
        viewModelScope.launch {
            try {
                repo.refreshRecentTracks()
            } catch (e: Exception) {
                recentTracksState.update(Result.Error(e))
            }
        }
    }
}