package de.schnettler.scrobbler.screens.local

import androidx.compose.foundation.Text
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.AlertDialog
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.Scrobble
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.util.copyByState

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
                    TextField(
                        value = trackState.value,
                        onValueChange = { trackState.value = it },
                        label = { Text(stringResource(id = R.string.edit_track)) })
                    Spacer(modifier = Modifier.preferredHeight(16.dp))
                    TextField(
                        value = artistState.value,
                        onValueChange = { artistState.value = it },
                        label = { Text(stringResource(id = R.string.edit_artist)) })
                    Spacer(modifier = Modifier.preferredHeight(16.dp))
                    TextField(
                        value = albumState.value,
                        onValueChange = { albumState.value = it },
                        label = { Text(stringResource(id = R.string.edit_album)) })
                }
            },
            confirmButton = {
                val updated = track.copyByState(trackState, artistState, albumState)
                TextButton(
                    onClick = { onSelect(if (updated != track) updated else null) },
                    contentColor = MaterialTheme.colors.secondary
                ) {
                    Text(text = stringResource(id = R.string.edit_save))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    contentColor = EmphasisAmbient.current.medium.applyEmphasis(
                        contentColor()
                    )
                ) {
                    Text(text = stringResource(id = R.string.edit_cancel))
                }
            }
        )
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    description: String,
    onDismiss: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss(false) },
        title = { Text(text = title) },
        text = { Text(text = description) },
        confirmButton = {
            TextButton(
                onClick = { onDismiss(true) },
                contentColor = MaterialTheme.colors.secondary
            ) {
                Text(text = stringResource(id = R.string.confirmdialog_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss(false) },
                contentColor = EmphasisAmbient.current.medium.applyEmphasis(contentColor())
            ) {
                Text(text = stringResource(id = R.string.confirmdialog_cancel))
            }
        }
    )
}