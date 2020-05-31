package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.*
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.repo.Repository
import de.schnettler.scrobbler.util.SessionStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
@FlowPreview
class MainViewModel(private val repo: Repository): ViewModel() {

    private val sessionResponse by lazy {
        repo.lastFmAuthProvider.sessionLive.asLiveData(viewModelScope.coroutineContext)
    }

    val sessionStatus: LiveData<SessionStatus> = Transformations.map(sessionResponse) {response ->
        when (response == null) {
            true -> SessionStatus.LoggedOut
            false -> SessionStatus.LoggedIn(response)
        }
    }

    fun onTokenReceived(token: String) {
        viewModelScope.launch {
            Timber.i("Refreshing Token")
            repo.lastFmAuthProvider.refreshSession(token)
        }
    }
}