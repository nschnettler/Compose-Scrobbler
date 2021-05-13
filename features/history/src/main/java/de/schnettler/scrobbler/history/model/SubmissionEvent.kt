package de.schnettler.scrobbler.history.model

import de.schnettler.scrobbler.model.Scrobble

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