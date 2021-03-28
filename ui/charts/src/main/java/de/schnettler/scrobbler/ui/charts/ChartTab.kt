package de.schnettler.scrobbler.ui.charts

import androidx.annotation.StringRes

enum class ChartTab(@StringRes val text: Int) {
    Artist(R.string.tab_artist),
    Track(R.string.tab_track)
}