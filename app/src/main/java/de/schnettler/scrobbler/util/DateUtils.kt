package de.schnettler.scrobbler.util

import android.icu.text.CompactDecimalFormat
import android.icu.text.DateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun milliSecondsToMinSeconds(input: Long) = String.format(
    "%d:%d",
    TimeUnit.MILLISECONDS.toMinutes(input),
    TimeUnit.MILLISECONDS.toSeconds(input) -
        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(input))
)

fun Long.milliSecondsToDate(): String =
    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault()).format(
        Date(this)
    )

fun Long.abbreviate(): String =
    CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)
        .format(this)