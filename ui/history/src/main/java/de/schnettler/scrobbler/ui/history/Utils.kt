package de.schnettler.scrobbler.ui.history

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.app.NotificationManagerCompat
import de.schnettler.database.models.Scrobble

fun Scrobble.copyByState(
    trackState: State<TextFieldValue>,
    artistState: State<TextFieldValue>,
    albumState: State<TextFieldValue>
) = this.copy(
    name = trackState.value.text,
    artist = artistState.value.text,
    album = albumState.value.text
)
fun Context.notificationListenerEnabled() =
    NotificationManagerCompat.getEnabledListenerPackages(this).contains(this.packageName)