package de.schnettler.scrobbler.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import de.schnettler.scrobbler.R

sealed class MenuAction(@StringRes val label: Int, @DrawableRes val icon: Int, val onClick: () -> Unit) {
    class Period(onClick: () -> Unit) : MenuAction(R.string.ic_period, R.drawable.ic_outline_event_24, onClick)
}