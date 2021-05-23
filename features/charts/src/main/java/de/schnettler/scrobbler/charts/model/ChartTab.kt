package de.schnettler.scrobbler.charts.model

import androidx.annotation.StringRes
import de.schnettler.scrobbler.charts.R

enum class ChartTab(@StringRes val text: Int) {
    Artist(R.string.tab_artist),
    Track(R.string.tab_track)
}