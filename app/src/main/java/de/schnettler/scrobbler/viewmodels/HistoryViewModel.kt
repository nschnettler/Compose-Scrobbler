package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.schnettler.repo.UserRepository

class HistoryViewModel @ViewModelInject constructor(repo: UserRepository) : ViewModel() {
    val recentTracks by lazy {
        repo.getUserRecentTrack().asLiveData(viewModelScope.coroutineContext)
    }
}