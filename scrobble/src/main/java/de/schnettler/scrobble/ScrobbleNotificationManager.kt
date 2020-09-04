package de.schnettler.scrobble

import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.Service
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import de.schnettler.database.models.Scrobble
import javax.inject.Inject

const val NOW_PLAYING_ID = 0
const val SCROBBLE_ID = 1
const val ERROR_ID = 2

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

        createChannel(
            context.getString(R.string.error_notification_channel_id),
            context.getString(R.string.error_notification_channel_name)
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

    private fun sendExpandableNotification(
        @StringRes channelId: Int,
        @StringRes title: Int,
        description: String,
        @DrawableRes icon: Int = R.drawable.ic_outline_album_24,
        priority: Int = NotificationCompat.PRIORITY_LOW,
        notificationId: Int,
        lines: Array<String>

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
                lines.reversed().forEach { line -> it.addLine(line) }
            })
        notificationManager.notify(notificationId, builder.build())
    }

    fun cancelNotifications(id: Int? = null) {
        if (id == null) {
            notificationManager.cancelAll()
        } else {
            notificationManager.cancel(id)
        }
    }

    fun updateNowPlayingNotification(track: Scrobble) {
        sendNotification(
            channelId = R.string.np_notification_channel_id,
            title = R.string.np_notification_channel_name,
            description = "${track.artist} - ${track.name}",
            notificationId = NOW_PLAYING_ID
        )
    }

    fun scrobbledNotification(lines: Array<String>, count: Int, description: String) {
        val fullDescription =
            if (count > 1) "$description + ${count - 1} more"
            else lines.first()

        sendExpandableNotification(
            channelId = R.string.scrobble_notification_channel_id,
            title = R.string.scrobble_title,
            description = fullDescription,
            notificationId = SCROBBLE_ID,
            lines = lines
        )
    }

    fun errorNotification(text: String) {
        sendNotification(
            channelId = R.string.error_notification_channel_id,
            title = R.string.error_title,
            description = text,
            icon = R.drawable.ic_outline_bug_report_24,
            notificationId = ERROR_ID
        )
    }
}