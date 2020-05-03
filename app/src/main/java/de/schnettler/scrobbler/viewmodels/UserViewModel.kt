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
}