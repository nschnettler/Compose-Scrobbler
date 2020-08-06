package de.schnettler.scrobbler.components

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.width
import androidx.ui.material.IconButton
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.outlined.AccountCircle
import androidx.ui.material.icons.rounded.Hearing
import androidx.ui.material.icons.rounded.PlayCircleOutline
import androidx.ui.unit.dp
import de.schnettler.database.models.Stats
import de.schnettler.scrobbler.util.formatter

@Composable
fun StatsRow(
    items: List<Pair<VectorAsset, Long?>>
) {
    Row(
        modifier = Modifier.fillMaxWidth() + Modifier.padding(bottom = 16.dp, top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach {
            val count = it.second ?: 0
            Column(horizontalGravity = Alignment.CenterHorizontally) {
                Icon(asset = it.first.copy(defaultHeight = 28.dp, defaultWidth = 28.dp))
                Text(text = formatter.format(count))
            }
        }
    }
}

@Composable
fun QuickActionsRow(items: List<Pair<VectorAsset, () -> Unit>>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        items.forEach {
            IconButton(onClick = { it.second.invoke() }) {
                Icon(asset = it.first)
            }
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}

@Composable
fun ListeningStats(item: Stats?) = StatsRow(
    items = listOf(
        Icons.Rounded.PlayCircleOutline to item?.plays,
        Icons.Rounded.Hearing to item?.userPlays,
        Icons.Outlined.AccountCircle to item?.listeners
    )
)