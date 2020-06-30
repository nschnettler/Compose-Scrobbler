package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.scrobbler.util.SessionState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
@FlowPreview
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