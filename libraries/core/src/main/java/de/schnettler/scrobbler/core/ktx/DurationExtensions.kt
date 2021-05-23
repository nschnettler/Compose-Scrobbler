package de.schnettler.scrobbler.core.ktx

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Duration.asMinSec() = this.toComponents { min, s, _ ->
    val padded = s.toString().padStart(2, '0')
    "$min:$padded"
}