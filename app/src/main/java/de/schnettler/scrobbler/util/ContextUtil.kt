package de.schnettler.scrobbler.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.NotificationManagerCompat

fun Context.notificationListenerEnabled() =
    NotificationManagerCompat.getEnabledListenerPackages(this).contains(this.packageName)

fun Context.openNotificationListenerSettings() =
    startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))

fun Context.openCustomTab(url: String) = CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(url))