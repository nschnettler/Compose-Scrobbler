package de.schnettler.scrobbler.components

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.layout.*
import androidx.ui.res.vectorResource
import androidx.ui.unit.dp
import de.schnettler.database.models.ListingMin
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.screens.formatter

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
fun ListeningStats(item: ListingMin) = StatsRow(items = listOf(
    R.drawable.ic_round_play_circle_outline_24 to item.plays,
    R.drawable.ic_round_hearing_24 to item.userPlays,
    R.drawable.ic_outline_account_circle_32 to item.listeners
))