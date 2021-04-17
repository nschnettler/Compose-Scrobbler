package de.schnettler.scrobbler.compose.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.ui.graphics.vector.ImageVector
import de.schnettler.scrobbler.compose.R

sealed class MenuAction(@StringRes val label: Int, val icon: ImageVector, val action: UIAction?) {
    object Period : MenuAction(R.string.ic_period, Icons.Rounded.Event, null)
    class OpenInBrowser(url: String) :
        MenuAction(R.string.ic_open_in, Icons.Rounded.OpenInBrowser, UIAction.OpenInBrowser(url))
}