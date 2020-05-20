package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.*
import de.schnettler.database.models.Session
import de.schnettler.repo.Repository
import timber.log.Timber

class UserViewModel(val repo: Repository) : ViewModel() {
    val session = MutableLiveData<Session>()

    val artists = Transformations.switchMap(session) { session ->
        repo.getUserTopArtists(session.key).asLiveData(viewModelScope.coroutineContext)
    }

    val userInfo= Transformations.switchMap(session) {session ->
        Timber.d("Loading: User Info")
        repo.getUserInfo(session.key).asLiveData(viewModelScope.coroutineContext)
    }

    val userTopAlbums= Transformations.switchMap(session) {session ->
        Timber.d("Loading: User Albums")
        repo.getUserTopAlbums(session.key).asLiveData(viewModelScope.coroutineContext)
    }

    val topTracks= Transformations.switchMap(session) {session ->
        Timber.d("Loading: User Tracks")
        repo.getUserTopTracks(session.key).asLiveData(viewModelScope.coroutineContext)
    }

    fun updateAuthState(new: Session) {
        if (session.value != new) {
            Timber.d("Session Changed")
            session.value = new
        }
    }
}