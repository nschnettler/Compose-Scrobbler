package de.schnettler.scrobbler.history.ktx

import android.content.Context
import androidx.core.app.NotificationManagerCompat

fun Context.notificationListenerEnabled() =
    NotificationManagerCompat.getEnabledListenerPackages(this).contains(this.packageName)