package de.schnettler.scrobbler.history.model

data class SubmissionResult(
    val accepted: List<Long>,
    val ignored: Map<Long, Long>,
    val error: String?
)