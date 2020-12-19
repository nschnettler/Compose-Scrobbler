package de.schnettler.scrobbler.screens.local

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.NotificationImportant
import androidx.compose.ui.graphics.vector.ImageVector
import de.schnettler.common.BuildConfig
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.ui.common.compose.UIAction
import de.schnettler.scrobbler.ui.common.util.AUTH_ENDPOINT
import de.schnettler.scrobbler.ui.common.util.REDIRECT_URL

enum class HistoryError(
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int,
    val icon: ImageVector,
    val action: UIAction
) {
    NotificationAccessDisabled(
        R.string.error_listener_title,
        R.string.error_listener_subtitle,
        Icons.Outlined.NotificationImportant,
        UIAction.OpenNotificationListenerSettings
    ),
    LoggedOut(
        R.string.error_login_title,
        R.string.error_login_subtitle,
        Icons.Outlined.AccountCircle,
        UIAction.OpenInBrowser("$AUTH_ENDPOINT?api_key=${BuildConfig.LASTFM_API_KEY}&cb=$REDIRECT_URL")
    )
}