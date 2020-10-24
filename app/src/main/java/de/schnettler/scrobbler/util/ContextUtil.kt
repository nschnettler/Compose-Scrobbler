package de.schnettler.scrobbler.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Context.openUrlInCustomTab(url: String) {
    val builder = CustomTabsIntent.Builder()
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(this, Uri.parse(url))
}

suspend fun Context.getMediaBrowserServices(): Map<String, String> {
    return withContext(Dispatchers.Default) {
        packageManager.queryIntentServices(
            Intent("android.media.browse.MediaBrowserService"),
            PackageManager.GET_RESOLVED_FILTER
        ).mapNotNull { it.serviceInfo }.associateBy({ it.packageName }, {
            it.loadLabel(packageManager).toString()
        })
    }
}

fun Context.notificationListenerEnabled() =
    NotificationManagerCompat.getEnabledListenerPackages(this).contains(this.packageName)