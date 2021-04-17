package de.schnettler.scrobbler.ui.detail.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.compose.widget.StatsRow
import de.schnettler.scrobbler.core.model.Stats

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