package de.schnettler.scrobbler.history.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import de.schnettler.scrobbler.history.R

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
        confirmButton = { PositiveButton(textRes = R.string.confirmdialog_confirm, onPressed = { onDismiss(true) }) },
        dismissButton = { NegativeButton(textRes = R.string.confirmdialog_cancel, onPressed = { onDismiss(false) }) }
    )
}