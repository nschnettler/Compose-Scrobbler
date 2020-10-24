package de.schnettler.scrobbler.screens.local

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun ErrorItem(item: HistoryError, onSelect: () -> Unit) {
    ListItem(
        text = { Text(text = stringResource(id = item.titleRes)) },
        secondaryText = { Text(text = stringResource(id = item.subtitleRes)) },
        singleLineSecondaryText = false,
        icon = {
            Box(alignment = Alignment.Center, modifier = Modifier.size(40.dp)) {
                Icon(asset = item.icon)
            }
        },
        modifier = Modifier.clickable(onClick = onSelect)
    )
}