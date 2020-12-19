package de.schnettler.scrobbler.util

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.ui.graphics.vector.ImageVector
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.ui.common.compose.UIAction

sealed class MenuAction(@StringRes val label: Int, val icon: ImageVector, val action: UIAction?) {
    object Period : MenuAction(R.string.ic_period, Icons.Rounded.Event, null)
    class OpenInBrowser(url: String) :
        MenuAction(R.string.ic_open_in, Icons.Rounded.OpenInBrowser, UIAction.OpenInBrowser(url))
}