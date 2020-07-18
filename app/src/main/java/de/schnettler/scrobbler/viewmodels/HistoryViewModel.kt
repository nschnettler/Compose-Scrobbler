package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.database.models.Track
import de.schnettler.repo.UserRepository
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.repo.Result
import de.schnettler.scrobbler.util.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel @ViewModelInject constructor(private val repo: UserRepository) : ViewModel() {
    val recentTracksState: MutableStateFlow<RefreshableUiState<List<Track>>> =
            MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))

    init {
        refreshHistory()
    }

    fun refreshHistory() {
        recentTracksState.update(Result.Loading)
        viewModelScope.launch {
            val result = try {
                Result.Success(repo.getUserRecentTrack())
            } catch (e: Exception) {
                Result.Error(e)
            }
            recentTracksState.update(result)
        }
    }
}