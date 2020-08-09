package de.schnettler.scrobbler.util

sealed class SessionState {
    object LoggedIn : SessionState()
    object LoggedOut : SessionState()
}