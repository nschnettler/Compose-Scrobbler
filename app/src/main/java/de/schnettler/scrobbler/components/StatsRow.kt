package de.schnettler.scrobbler.components

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.Stats
import de.schnettler.scrobbler.util.abbreviate

@Composable
fun StatsRow(
    items: List<Pair<VectorAsset, Long?>>
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach {
            val count = it.second ?: 0
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(asset = it.first.copy(defaultHeight = 28.dp, defaultWidth = 28.dp))
                Text(text = count.abbreviate())
            }
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