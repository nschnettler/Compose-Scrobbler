package de.schnettler.scrobbler.util

import com.etiennelenhart.eiffel.state.ViewState
import de.schnettler.database.models.Artist
import de.schnettler.database.models.Session


sealed class SessionStatus {
    class LoggedIn(val session: Session) : SessionStatus()
    object LoggedOut : SessionStatus()
}

data class ChartState(
    val artistState: State<List<Artist>>
): ViewState

data class State<T>(var data: T?, var status: LoadingStatus)

sealed class LoadingStatus {
    object Init: LoadingStatus()
    object Loading: LoadingStatus()
    class Error(errorMessage: String?) : LoadingStatus()
}