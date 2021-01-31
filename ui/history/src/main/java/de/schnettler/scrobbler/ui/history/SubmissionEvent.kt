package de.schnettler.scrobbler.ui.history

import de.schnettler.database.models.Scrobble

sealed class SubmissionEvent {
    class Success(
        val result: SubmissionResult
    ) : SubmissionEvent()

    class ShowDetails(
        val accepted: List<Scrobble>,
        val ignored: Map<Scrobble, Int>,
        val errorMessage: String?
    ) : SubmissionEvent()
}