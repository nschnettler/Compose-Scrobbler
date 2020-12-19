package de.schnettler.scrobbler.ui.common.util

import android.icu.text.CompactDecimalFormat
import android.icu.text.DateFormat
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

fun Long.milliSecondsToDate(): String =
    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault()).format(
        Date(this)
    )

fun Long.abbreviate(): String =
    CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)
        .format(this)

@ExperimentalTime
fun Duration.asMinSec() = this.toComponents { min, s, _ ->
    val padded = s.toString().padStart(2, '0')
    "$min:$padded"
}