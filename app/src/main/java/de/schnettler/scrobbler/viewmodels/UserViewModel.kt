package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.schnettler.database.models.Session
import de.schnettler.repo.Repository

class UserViewModel(val session: Session, repo: Repository) : ViewModel() {
    val userInfo by lazy {
        repo.getUserInfo(session.key).asLiveData(viewModelScope.coroutineContext)
    }

    val userTopArtists by lazy {
        repo.getUserTopArtists(session.key).asLiveData(viewModelScope.coroutineContext)
    }

    val userTopAlbums by lazy {
        repo.getUserTopAlbums(session.key).asLiveData(viewModelScope.coroutineContext)
    }

    val topTracks by lazy {
        repo.getUserTopTracks(session.key).asLiveData(viewModelScope.coroutineContext)
    }

    val getUserRecentTracks by lazy {
        repo.getUserRecentTrack(session.key).asLiveData(viewModelScope.coroutineContext)
    }
}