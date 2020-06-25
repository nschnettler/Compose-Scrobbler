package de.schnettler.scrobbler.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import de.schnettler.database.models.ListingMin
import de.schnettler.scrobbler.R

sealed class MenuAction(@StringRes val label: Int, @DrawableRes val icon: Int) {
    class Period(val onClick: () -> Unit) : MenuAction(R.string.ic_period, R.drawable.ic_outline_event_24)
    class OpenInBrowser(val onClick: (ListingMin) -> Unit): MenuAction(R.string.ic_open_in, R.drawable.ic_round_open_in_browser_24)
}