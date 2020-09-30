package de.schnettler.scrobbler.util

import androidx.compose.runtime.State
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.Scrobble
import kotlin.math.roundToInt

val defaultSpacerSize = 16.dp

fun Scrobble.copyByState(
    trackState: State<TextFieldValue>,
    artistState: State<TextFieldValue>,
    albumState: State<TextFieldValue>
) = this.copy(
    name = trackState.value.text,
    artist = artistState.value.text,
    album = albumState.value.text
)

enum class Orientation {
    Vertical,
    Horizontal
}

fun runtimeInfo(played: Long, duration: Long): String {
    return "${milliSecondsToMinSeconds(played)}/${
        milliSecondsToMinSeconds(duration)
    } (${(played.toFloat() / duration * 100).roundToInt()}%)"
}