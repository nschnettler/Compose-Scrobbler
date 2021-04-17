package de.schnettler.scrobbler.history.ktx

import androidx.compose.runtime.State
import androidx.compose.ui.text.input.TextFieldValue
import de.schnettler.scrobbler.model.Scrobble

fun Scrobble.copyByState(
    trackState: State<TextFieldValue>,
    artistState: State<TextFieldValue>,
    albumState: State<TextFieldValue>
) = this.copy(
    name = trackState.value.text,
    artist = artistState.value.text,
    album = albumState.value.text
)