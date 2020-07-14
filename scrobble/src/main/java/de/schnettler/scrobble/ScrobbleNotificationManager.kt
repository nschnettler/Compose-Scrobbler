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

const val NOW_PLAYING_ID  = 0
const val SCROBBLE_ID = 1

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
            @StringRes channelId: Int,
            @StringRes title: Int,
            description: String,
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
                .setContentText(description)
                .setPriority(priority)
                .setAutoCancel(true)
        notificationManager.notify(notificationId, builder.build())
    }

    private fun sendNotification2(
        @StringRes channelId: Int,
        @StringRes title: Int,
        description: String,
        @DrawableRes icon: Int = R.drawable.ic_outline_album_24,
        priority: Int = NotificationCompat.PRIORITY_LOW,
        notificationId: Int,
        lines: List<String>

    ) {
        val builder = NotificationCompat.Builder(
            context,
            context.getString(channelId)
        )
            .setSmallIcon(icon)
            .setContentTitle(context.getString(title))
            .setContentText(description)
            .setPriority(priority)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.InboxStyle().also {
                lines.forEach { line -> it.addLine(line) }
            }.addLine("Test"))
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
                channelId = R.string.np_notification_channel_id,
                title = R.string.np_notification_channel_name,
                description = "${track.artist} - ${track.name}",
                notificationId = NOW_PLAYING_ID
        )
    }

    fun scrobbledNotification(lines: List<String>, count: Int) {
        val append = if(count > lines.size) " + $count more" else ""
        sendNotification2(
                channelId = R.string.scrobble_notification_channel_id,
                title = R.string.scrobble_title,
                description = "${lines.first()}$append",
                notificationId = SCROBBLE_ID,
            lines = lines
        )
    }
}