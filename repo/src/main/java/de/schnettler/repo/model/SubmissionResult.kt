package de.schnettler.repo.model

import de.schnettler.lastfm.models.Errors
import de.schnettler.lastfm.models.ScrobbleResponse

data class SubmissionResult(
    val accepted: List<ScrobbleResponse>,
    val ignored: List<ScrobbleResponse>,
    val errors: List<Errors>
)