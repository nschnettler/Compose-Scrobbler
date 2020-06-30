package de.schnettler.scrobbler.util

import de.schnettler.database.models.Session


sealed class SessionStatus {
    class LoggedIn(val session: Session) : SessionStatus()
    object LoggedOut : SessionStatus()
}