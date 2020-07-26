package de.schnettler.scrobbler.components

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.width
import androidx.ui.material.IconButton
import androidx.ui.res.vectorResource
import androidx.ui.unit.dp
import de.schnettler.database.models.LastFmStatsEntity
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.util.formatter

@Composable
fun StatsRow(
    items: List<Pair<Int, Long>>
) {
    Row(
        modifier = Modifier.fillMaxWidth() + Modifier.padding(bottom = 16.dp, top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach {
            Column(horizontalGravity = Alignment.CenterHorizontally) {
                Icon(asset = vectorResource(id = it.first))
                Text(text = formatter.format(it.second))
            }
        }
    }
}

@Composable
fun QuickActionsRow(items: List<Pair<Int, () -> Unit>>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        items.forEach {
            IconButton(onClick = { it.second.invoke() }) {
                Icon(asset = vectorResource(id = it.first))
            }
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}

@Composable
fun ListeningStats(item: LastFmStatsEntity) = StatsRow(
    items = listOf(
        R.drawable.ic_round_play_circle_outline_24 to item.plays,
        R.drawable.ic_round_hearing_24 to item.userPlays,
        R.drawable.ic_outline_account_circle_32 to item.listeners
    )
)