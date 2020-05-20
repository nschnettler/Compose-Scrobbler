package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.schnettler.repo.Repository

class HistoryViewModel(repo: Repository) : ViewModel() {
    val recentTracks by lazy {
        repo.getUserRecentTrack().asLiveData(viewModelScope.coroutineContext)
    }
}