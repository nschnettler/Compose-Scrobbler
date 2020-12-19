package de.schnettler.scrobbler.util

import androidx.compose.runtime.State
import androidx.compose.ui.text.input.TextFieldValue
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

 fun <T> List<T>.secondOrNull(): T? {
    return if (size < 2) null else this[1]
}