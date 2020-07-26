package de.schnettler.scrobbler.util

import de.schnettler.database.models.Session

sealed class SessionState {
    class LoggedIn(val session: Session) : SessionState()
    object LoggedOut : SessionState()
}