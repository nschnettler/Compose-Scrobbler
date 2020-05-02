package de.schnettler.scrobbler.viewmodels

import android.content.Context
import androidx.lifecycle.*
import de.schnettler.repo.Repository
import de.schnettler.scrobbler.util.SessionStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
@FlowPreview
class MainViewModel(val repo: Repository): ViewModel() {
    val topArtists by lazy {
        repo.getTopArtists().asLiveData()
    }

    private val session by lazy {
        repo.getSession()
    }

    val sessionStatus= Transformations.map(session) {session ->
        if (session == null) SessionStatus.LoggedOut else SessionStatus.LoggedIn(session)
    }

    fun onTokenReceived(token: String) {
        viewModelScope.launch {
            Timber.i("Refreshing Token")
            repo.refreshSession(token)
        }
    }
}