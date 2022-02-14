package de.schnettler.scrobbler.history.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.compose.theme.AppColor
import de.schnettler.scrobbler.compose.widget.CustomDivider
import de.schnettler.scrobbler.compose.widget.MaterialListItem
import de.schnettler.scrobbler.compose.widget.Orientation
import de.schnettler.scrobbler.compose.widget.Spacer
import de.schnettler.scrobbler.history.R
import de.schnettler.scrobbler.history.ui.widget.SubmissionResultScrobbleItem
import de.schnettler.scrobbler.model.Scrobble
import kotlin.time.ExperimentalTime

@Composable
fun SubmissionResultDetailsDialog(
    title: String,
    accepted: List<Scrobble>,
    rejected: Map<Scrobble, Int>,
    errorMessage: String?,
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

                        Spacer(size = 16.dp, orientation = Orientation.Horizontal)

                        errorMessage?.let { ErrorCategory(errorMessage = errorMessage) }
                    }
                }
            },
            confirmButton = {
                PositiveButton(
                    textRes = R.string.confirmdialog_confirm,
                    onPressed = {
                        shown = false
                        onDismiss(true)
                    })
            },
            dismissButton = { NegativeButton(textRes = R.string.confirmdialog_cancel, onPressed = { shown = false }) }
        )
    }
}

@OptIn(ExperimentalTime::class, ExperimentalMaterialApi::class)
@Composable
fun AcceptedCategory(accepted: List<Scrobble>) {
    var expanded by remember { mutableStateOf(false) }
    MaterialListItem(
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

@Composable
fun ErrorCategory(errorMessage: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = AppColor.Error.copy(0.4F),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
        )
    }
}

@OptIn(ExperimentalTime::class, ExperimentalMaterialApi::class)
@Composable
fun IgnoredCategory(rejected: Map<Scrobble, Int>) {
    var expanded by remember { mutableStateOf(false) }
    MaterialListItem(
        text = { Text(text = "Rejected") },
        secondaryText = {
            Column {
                if (rejected.isEmpty()) {
                    Text(text = "No Scrobbles were rejected")
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