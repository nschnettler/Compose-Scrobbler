package de.schnettler.scrobbler.history.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.NotificationImportant
import androidx.compose.ui.graphics.vector.ImageVector
import de.schnettler.scrobbler.compose.navigation.UIAction
import de.schnettler.scrobbler.core.BuildConfig
import de.schnettler.scrobbler.core.util.AUTH_ENDPOINT
import de.schnettler.scrobbler.core.util.REDIRECT_URL
import de.schnettler.scrobbler.history.R

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