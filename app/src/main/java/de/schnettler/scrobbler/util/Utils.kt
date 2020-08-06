package de.schnettler.scrobbler.util

import androidx.compose.State
import androidx.ui.input.TextFieldValue
import androidx.ui.unit.dp
import de.schnettler.database.models.LocalTrack

val defaultSpacerSize = 16.dp

fun LocalTrack.copyByState(
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