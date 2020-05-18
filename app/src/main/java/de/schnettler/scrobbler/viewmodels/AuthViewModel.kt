package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.*
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.AuthToken
import de.schnettler.repo.Repository

class AuthViewModel(repo: Repository): ViewModel() {
    val spotifyToken = repo.getSpotifyAuthToken.asLiveData(viewModelScope.coroutineContext)

    val authStatus: LiveData<AuthStatus> = Transformations.map(spotifyToken) {response ->
        when(response) {
            is StoreResponse.Data -> {
                AuthStatus.Authenticated(response.value)
            }
            else -> AuthStatus.LoggedOut
        }
    }
}

sealed class AuthStatus() {
    data class Authenticated(val token: AuthToken) : AuthStatus() {
        fun loggedIn() = token.isValid()
    }
    object LoggedOut: AuthStatus()
}