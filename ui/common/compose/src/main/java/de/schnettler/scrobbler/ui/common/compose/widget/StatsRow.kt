package de.schnettler.scrobbler.ui.common.compose.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.Stats
import de.schnettler.scrobbler.ui.common.compose.util.ThemedPreview
import de.schnettler.scrobbler.ui.common.util.abbreviate

@Composable
fun StatsRow(
    items: List<Pair<ImageVector, Long>>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.filter { it.second >= 0 }.forEach {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = it.first, null, modifier = Modifier.size(28.dp))
                Text(text = it.second.abbreviate())
            }
        }
    }
}

@Composable
fun ListeningStats(item: Stats?) {
    item?.let { stat ->
        StatsRow(
            items = listOf(
                Icons.Rounded.PlayCircleOutline to stat.plays,
                Icons.Rounded.Hearing to stat.userPlays,
                Icons.Outlined.AccountCircle to stat.listeners
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

// Preview

@Preview
@Composable
fun StatsRowPreview() = ThemedPreview {
    StatsRow(items = listOf(
        Pair(Icons.Rounded.PlayCircleOutline, 10L),
        Pair(Icons.Rounded.Hearing, 546461L),
        Pair(Icons.Rounded.Person, 5464656451L)
    ))
}