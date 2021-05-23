package de.schnettler.scrobbler

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.database.daos.SessionDao
import de.schnettler.lastfm.api.lastfm.SessionService
import de.schnettler.repo.authentication.provider.LastFmAuthProviderImpl
import de.schnettler.repo.mapping.auth.SessionMapper
import de.schnettler.scrobbler.model.SessionState
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authProvider: LastFmAuthProviderImpl,
    private val sessionService: SessionService,
    private val sessionDao: SessionDao
) : ViewModel() {

    private val sessionResponse by lazy {
        authProvider.sessionLive.asLiveData(viewModelScope.coroutineContext)
    }

    val sessionStatus: LiveData<SessionState> = Transformations.map(sessionResponse) { response ->
        when (response == null) {
            true -> SessionState.LoggedOut
            false -> SessionState.LoggedIn
        }
    }

    fun onTokenReceived(token: String) {
        viewModelScope.launch {
            Timber.i("Refreshing Token")
            val session = SessionMapper.map(sessionService.getSession(token))
            sessionDao.forceInsert(session)
        }
    }
}