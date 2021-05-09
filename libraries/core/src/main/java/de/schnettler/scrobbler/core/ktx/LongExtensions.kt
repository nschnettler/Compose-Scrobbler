package de.schnettler.scrobbler.core.ktx

import android.icu.text.CompactDecimalFormat
import android.icu.text.DateFormat
import java.util.Locale
import java.util.Date

fun Long.milliSecondsToDate(): String =
    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault()).format(
        Date(this)
    )

fun Long.abbreviate(): String =
    CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)
        .format(this)

fun Long.toBoolean() = this == 1L