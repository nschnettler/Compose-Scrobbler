package de.schnettler.scrobbler.submission.model

import de.schnettler.lastfm.models.Errors

data class SubmissionResult(
    val accepted: List<ScrobbleResponse>,
    val ignored: List<ScrobbleResponse>,
    val errors: List<Errors>,
    val exceptions: List<Throwable>
)