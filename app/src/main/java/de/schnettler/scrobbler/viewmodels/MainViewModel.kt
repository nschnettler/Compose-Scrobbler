package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.scrobbler.util.SessionState
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel @ViewModelInject constructor(
    private val authProvider: LastFmAuthProvider
): ViewModel() {

    private val sessionResponse by lazy {
        authProvider.sessionLive.asLiveData(viewModelScope.coroutineContext)
    }

    val sessionStatus: LiveData<SessionState> = Transformations.map(sessionResponse) { response ->
        when (response == null) {
            true -> SessionState.LoggedOut
            false -> SessionState.LoggedIn(response)
        }
    }

    fun onTokenReceived(token: String) {
        viewModelScope.launch {
            Timber.i("Refreshing Token")
            authProvider.refreshSession(token)
        }
    }
}