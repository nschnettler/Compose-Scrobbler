package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.schnettler.database.models.Session
import de.schnettler.repo.Repository

class HistoryViewModel(val session: Session, repo: Repository) : ViewModel() {
    val recentTracks by lazy {
        repo.getUserRecentTrack(session.key).asLiveData(viewModelScope.coroutineContext)
    }
}