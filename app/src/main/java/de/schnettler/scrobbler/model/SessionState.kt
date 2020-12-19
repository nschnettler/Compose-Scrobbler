package de.schnettler.scrobbler.model

sealed class SessionState {
    object LoggedIn : SessionState()
    object LoggedOut : SessionState()
}