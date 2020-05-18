package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.*
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.fresh
import de.schnettler.database.models.AuthToken
import de.schnettler.database.models.AuthTokenType
import de.schnettler.database.models.Listing
import de.schnettler.database.models.Session
import de.schnettler.repo.Repository
import de.schnettler.scrobbler.util.DoubleTrigger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class UserViewModel(val repo: Repository) : ViewModel() {
    val authState = MutableLiveData<AuthState>()

    val artists = Transformations.switchMap(authState) { state ->
        repo.getUserTopArtists(state.session.key, state.spotifyAuthToken).asLiveData(viewModelScope.coroutineContext)
    }

    val userInfo= Transformations.switchMap(authState) {state ->
        Timber.d("Loading: User Info")
        repo.getUserInfo(state.session.key).asLiveData(viewModelScope.coroutineContext)
    }

    val userTopAlbums= Transformations.switchMap(authState) {state ->
        Timber.d("Loading: User Albums")
        repo.getUserTopAlbums(state.session.key).asLiveData(viewModelScope.coroutineContext)
    }

    val topTracks= Transformations.switchMap(authState) {state ->
        Timber.d("Loading: User Tracks")
        repo.getUserTopTracks(state.session.key).asLiveData(viewModelScope.coroutineContext)
    }

    fun refreshSpotifyToken() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.spotifyAuthStore.clear(AuthTokenType.Spotify.value)
                repo.spotifyAuthStore.fresh(AuthTokenType.Spotify.value)
            }
        }
    }

    fun updateAuthState(new: AuthState) {
        if (authState.value != new) {
            Timber.d("State Changed: Session ${new.session} - Auth ${new.spotifyAuthToken}")
            authState.value = new
        }
    }
}

data class AuthState(val session: Session, val spotifyAuthToken: AuthToken)