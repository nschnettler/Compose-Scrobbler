package de.schnettler.scrobbler.screens.local

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.state
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.Scrobble
import de.schnettler.scrobbler.components.CustomDivider
import de.schnettler.scrobbler.components.NameListIcon
import de.schnettler.scrobbler.util.ScrobbleAction
import de.schnettler.scrobbler.util.milliSecondsToDate
import de.schnettler.scrobbler.util.milliSecondsToMinSeconds
import de.schnettler.scrobbler.util.packageNameToAppName
import kotlin.math.roundToInt

@Composable
fun ScrobbleItem(track: Scrobble, onActionClicked: (ScrobbleAction) -> Unit) {
    var expanded by state { false }
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
                        amountPlayed = track.amountPlayed,
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
        onClick = { expanded = !expanded },
        trailing = {
            track.timestampToRelativeTime()?.let {
                Column(horizontalGravity = Alignment.End) {
                    Text(text = it)
                    if (track.isCached()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Icon(asset = Icons.Rounded.CloudOff.copy(defaultWidth = 16.dp, defaultHeight = 16.dp))
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
    amountPlayed: Long,
    duration: Long,
    timestamp: Long
) {
    Spacer(modifier = Modifier.preferredHeight(8.dp))
    Text(text = "Source: ${packageNameToAppName(playedBy)}")
    Text(
        text = "Runtime: ${milliSecondsToMinSeconds(amountPlayed)}/${
            milliSecondsToMinSeconds(duration)
        } (${(amountPlayed.toFloat() / duration * 100).roundToInt()}%)"
    )
    Text(text = "Timestamp: ${(timestamp * 1000).milliSecondsToDate()}")
    Spacer(modifier = Modifier.preferredHeight(8.dp))
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