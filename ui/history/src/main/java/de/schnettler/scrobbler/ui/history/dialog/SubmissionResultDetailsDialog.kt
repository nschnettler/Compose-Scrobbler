package de.schnettler.scrobbler.ui.history.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.AlertDialog
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.schnettler.database.models.Scrobble
import de.schnettler.scrobbler.ui.common.compose.widget.CustomDivider
import de.schnettler.scrobbler.ui.history.R
import de.schnettler.scrobbler.ui.history.widet.SubmissionResultScrobbleItem
import kotlin.time.ExperimentalTime

@Composable
fun SubmissionResultDetailsDialog(
    title: String,
    accepted: List<Scrobble>,
    rejected: Map<Scrobble, Int>,
    onDismiss: (Boolean) -> Unit
) {
    var shown by remember { mutableStateOf(true) }
    if (shown) {
        AlertDialog(
            onDismissRequest = { onDismiss(false) },
            title = { Text(text = title) },
            text = {
                LazyColumn {
                    item {
                        AcceptedCategory(accepted = accepted)
                        CustomDivider()
                        IgnoredCategory(rejected = rejected)
                    }
                }
            },
            confirmButton = {
                PositiveButton(
                    textRes = R.string.confirmdialog_confirm,
                    onPressed = { onDismiss(true) })
            },
            dismissButton = { NegativeButton(textRes = R.string.confirmdialog_cancel, onPressed = { shown = false }) }
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun AcceptedCategory(accepted: List<Scrobble>) {
    var expanded by remember { mutableStateOf(false) }
    ListItem(
        text = { Text(text = "Accepted") },
        secondaryText = {
            Column {
                if (accepted.isEmpty()) {
                    Text(text = "No Scrobbles were accepted")
                } else {
                    Text(text = "${accepted.size} Scrobbles were accepted")
                }
                if (expanded) {
                    accepted.forEach {
                        SubmissionResultScrobbleItem(track = it, onActionClicked = { }, reason = "Was submitted")
                    }
                }
            }
        },
        modifier = Modifier.clickable { expanded = !expanded }
    )
}

@OptIn(ExperimentalTime::class)
@Composable
fun IgnoredCategory(rejected: Map<Scrobble, Int>) {
    var expanded by remember { mutableStateOf(false) }
    ListItem(
        text = { Text(text = "Rejected") },
        secondaryText = {
            Column {
                if (rejected.isEmpty()) {
                    Text(text = "All Scrobbles were accepted")
                } else {
                    Text(text = "${rejected.size} Scrobbles were rejected")
                }
                if (expanded) {
                    rejected.forEach {
                        SubmissionResultScrobbleItem(
                            track = it.key,
                            onActionClicked = { /*TODO*/ },
                            reason = stringResource(id = it.value)
                        )
                    }
                }
            }
        },
        modifier = Modifier.clickable { expanded = !expanded }
    )
}