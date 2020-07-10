package de.schnettler.scrobble

import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.Service
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import de.schnettler.database.models.LocalTrack
import javax.inject.Inject

val NOW_PLAYING_ID  = 0
val SCROBBLE_ID = 1

class ScrobbleNotificationManager @Inject constructor(
        private val context: Service,
        private val notificationManager: android.app.NotificationManager
) {
    init {
        createChannel(
                context.getString(R.string.np_notification_channel_id),
                context.getString(R.string.np_notification_channel_name)
        )

        createChannel(
                context.getString(R.string.scrobble_notification_channel_id),
                context.getString(R.string.scrobble_notification_channel_name)
        )
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channel = NotificationChannel(
                    channelId,
                    channelName,
                    IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.np_description)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(
            track: LocalTrack,
            @StringRes channelId: Int,
            @StringRes title: Int,
            @DrawableRes icon: Int = R.drawable.ic_outline_album_24,
            priority: Int = NotificationCompat.PRIORITY_LOW,
            notificationId: Int

    ) {
        val builder = NotificationCompat.Builder(
                context,
                context.getString(channelId)
        )
                .setSmallIcon(icon)
                .setContentTitle(context.getString(title))
                .setContentText("${track.artist} - ${track.title}")
                .setPriority(priority)
                .setAutoCancel(true)
        notificationManager.notify(notificationId, builder.build())
    }

    fun cancelNotifications(id: Int? = null) {
        if (id == null) {
            notificationManager.cancelAll()
        } else {
            notificationManager.cancel(id)
        }
    }

    fun updateNowPlayingNotification(track: LocalTrack) {
        sendNotification(
                track = track,
                channelId = R.string.np_notification_channel_id,
                title = R.string.np_notification_channel_name,
                notificationId = NOW_PLAYING_ID
        )
    }

    fun scrobbledNotification(track: LocalTrack) {
        sendNotification(
                track = track,
                channelId = R.string.scrobble_notification_channel_id,
                title = R.string.scrobble_notification_channel_name,
                notificationId = SCROBBLE_ID
        )
    }
}