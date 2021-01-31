package de.schnettler.scrobbler.ui.history

import kotlin.collections.Map as IdToReasonMap

data class SubmissionResult(
    val accepted: List<Long>,
    val ignored: IdToReasonMap<Long, Long>
)