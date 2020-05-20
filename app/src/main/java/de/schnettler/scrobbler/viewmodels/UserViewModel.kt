package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.schnettler.repo.Repository

class UserViewModel(private val repo: Repository) : ViewModel() {

    val artists by lazy {
        repo.getUserTopArtists().asLiveData(viewModelScope.coroutineContext)
    }

    val userInfo by lazy {
        repo.getUserInfo().asLiveData(viewModelScope.coroutineContext)
    }

    val userTopAlbums by lazy {
        repo.getUserTopAlbums().asLiveData(viewModelScope.coroutineContext)
    }

    val topTracks by lazy {
        repo.getUserTopTracks().asLiveData(viewModelScope.coroutineContext)
    }
}