package de.schnettler.scrobbler.screens.local

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.HapticFeedBackAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.Scrobble
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.CustomDivider
import de.schnettler.scrobbler.components.NameListIcon
import de.schnettler.scrobbler.util.ScrobbleAction
import de.schnettler.scrobbler.util.milliSecondsToDate
import de.schnettler.scrobbler.util.packageNameToAppName
import de.schnettler.scrobbler.util.runtimeInfo

@Composable
fun ScrobbleItem(track: Scrobble, onActionClicked: (ScrobbleAction) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val feedback = HapticFeedBackAmbient.current
    ListItem(
        text = { Text(text = track.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        secondaryText = {
            Column {
                Text(
                    text = "${track.artist} ⦁ ${track.album}",
                    maxLines = if (expanded) 3 else 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (expanded) {
                    if (track.isLocal()) AdditionalInformation(
                        playedBy = track.playedBy,
                        played = track.amountPlayed,
                        duration = track.duration,
                        timestamp = track.timestamp
                    )
                    else Spacer(modifier = Modifier.preferredHeight(16.dp))
                    CustomDivider()
                    QuickActions(track.isCached(), onActionClicked)
                }
            }
        },
        icon = { NameListIcon(title = track.name) },
        modifier = Modifier.clickable(onClick = { onActionClicked(ScrobbleAction.OPEN) }, onLongClick = {
            expanded = !expanded
            feedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }),
        trailing = {
            track.timestampToRelativeTime()?.let {
                Column(verticalArrangement = Arrangement.Center) {
                    Text(text = it)
                    if (track.isCached()) {
                        Surface(
                            color = MaterialTheme.colors.secondary,
                            shape = CircleShape,
                            modifier = Modifier.padding(top = 8.dp, end = 16.dp).preferredSize(8.dp).gravity(
                                Alignment.End
                            )
                        ) { }
                    }
                }
            }
        }
    )
    CustomDivider()
}

@Composable
fun AdditionalInformation(
    playedBy: String,
    played: Long,
    duration: Long,
    timestamp: Long
) {
    Spacer(modifier = Modifier.preferredHeight(8.dp))
    InformationItem(category = stringResource(id = R.string.scrobble_source), value = packageNameToAppName(playedBy))
    InformationItem(category = stringResource(id = R.string.scrobble_runtime), value = runtimeInfo(played, duration))
    InformationItem(category = stringResource(id = R.string.scrobble_timestamp),
        value = (timestamp * 1000).milliSecondsToDate()
    )
    Spacer(modifier = Modifier.preferredHeight(8.dp))
}

@Composable
private fun InformationItem(category: String, value: String) {
    Row {
        Text(text = "$category:")
        Spacer(modifier = Modifier.preferredWidth(4.dp))
        Text(text = value)
    }
}

@Composable
fun QuickActions(isCached: Boolean, onActionClicked: (ScrobbleAction) -> Unit) {
    val actions = mutableListOf<ScrobbleAction>()
    if (isCached) { actions.addAll(listOf(ScrobbleAction.EDIT, ScrobbleAction.DELETE, ScrobbleAction.SUBMIT)) }
    actions.add(ScrobbleAction.OPEN)
    QuickActionsRow(items = actions, onSelect = onActionClicked)
}

@Composable
private fun QuickActionsRow(items: List<ScrobbleAction>, onSelect: (ScrobbleAction) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        items.forEach {
            IconButton(onClick = { onSelect(it) }) {
                Icon(asset = it.asset)
            }
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}