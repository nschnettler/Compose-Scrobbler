package de.schnettler.scrobbler.history.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.history.R
import de.schnettler.scrobbler.history.ktx.copyByState
import de.schnettler.scrobbler.model.Scrobble

@Composable
fun TrackEditDialog(
    track: Scrobble?,
    onSelect: (selected: Scrobble?) -> Unit,
    onDismiss: () -> Unit
) {
    if (track != null) {
        val trackState = remember { mutableStateOf(TextFieldValue(track.name)) }
        val artistState = remember { mutableStateOf(TextFieldValue(track.artist)) }
        val albumState = remember { mutableStateOf(TextFieldValue(track.album)) }

        AlertDialog(
            onDismissRequest = {
                if (track.copyByState(trackState, artistState, albumState) == track) {
                    onDismiss()
                }
            },
            title = { Text(text = stringResource(id = R.string.edit_title)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = trackState.value,
                        onValueChange = { trackState.value = it },
                        label = { Text(stringResource(id = R.string.edit_track)) })
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = artistState.value,
                        onValueChange = { artistState.value = it },
                        label = { Text(stringResource(id = R.string.edit_artist)) })
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = albumState.value,
                        onValueChange = { albumState.value = it },
                        label = { Text(stringResource(id = R.string.edit_album)) })
                }
            },
            confirmButton = {
                val updated = track.copyByState(trackState, artistState, albumState)
                PositiveButton(
                    textRes = R.string.edit_save,
                    onPressed = { onSelect(if (updated != track) updated else null) })
            },
            dismissButton = { NegativeButton(textRes = R.string.edit_cancel, onPressed = onDismiss) }
        )
    }
}