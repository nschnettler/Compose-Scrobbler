package de.schnettler.scrobbler.util

import android.icu.text.CompactDecimalFormat
import android.icu.text.DateFormat
import java.util.Date
import java.util.Locale

fun Long.milliSecondsToDate(): String =
    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault()).format(
        Date(this)
    )

fun Long.abbreviate(): String =
    CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)
        .format(this)