package de.schnettler.scrobbler.util

import androidx.compose.runtime.State
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.Scrobble
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

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

@ExperimentalTime
fun Duration.asMinSec() = this.toComponents { min, s, _ ->
    val padded = s.toString().padStart(2, '0')
    "$min:$padded"
}

 fun <T> List<T>.secondOrNull(): T? {
    return if (size < 2) null else this[1]
}