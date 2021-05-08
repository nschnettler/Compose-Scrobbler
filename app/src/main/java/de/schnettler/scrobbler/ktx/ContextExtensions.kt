package de.schnettler.scrobbler.ktx

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

fun Context.openNotificationListenerSettings() =
    startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))

fun Context.openCustomTab(url: String) = CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(url))