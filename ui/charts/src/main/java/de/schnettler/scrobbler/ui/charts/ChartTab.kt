package de.schnettler.scrobbler.ui.charts

import androidx.annotation.StringRes

enum class ChartTab(val index: Int, @StringRes val text: Int) {
    Artist(0, R.string.tab_artist),
    Track(1, R.string.tab_track)
}