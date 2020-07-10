package de.schnettler.scrobble

import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.Service
import android.os.Build
import androidx.core.app.NotificationCompat
import de.schnettler.database.models.LocalTrack
import javax.inject.Inject

val NOW_PLAYING_ID  = 0
class ScrobbleNotificationManager @Inject constructor(
        private val context: Service,
        private val notificationManager: android.app.NotificationManager
) {
    init {
        createChannel(
                context.getString(R.string.np_notification_channel_id),
                context.getString(R.string.np_notification_channel_name)
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

    private fun sendNotification(track: LocalTrack) {
        val builder = NotificationCompat.Builder(
                context,
                context.getString(R.string.np_notification_channel_id)
        )
                .setSmallIcon(R.drawable.ic_outline_album_24)
                .setContentTitle(context.getString(R.string.np_title))
                .setContentText("${track.artist} - ${track.title}")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)
        notificationManager.notify(NOW_PLAYING_ID, builder.build())
    }

    fun cancelNotifications(id: Int? = null) {
        if (id == null) {
            notificationManager.cancelAll()
        } else {
            notificationManager.cancel(id)
        }
    }

    fun updateNowPlayingNotification(track: LocalTrack) {
        sendNotification(track)
    }
}