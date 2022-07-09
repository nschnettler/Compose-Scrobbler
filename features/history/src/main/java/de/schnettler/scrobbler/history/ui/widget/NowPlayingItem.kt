@file:OptIn(ExperimentalMaterial3Api::class)

package de.schnettler.scrobbler.history.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.compose.widget.MaterialListItem
import de.schnettler.scrobbler.compose.widget.PlainListIconBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingItem(name: String, artist: String, onClick: () -> Unit) {
    Card(modifier = Modifier.padding(16.dp)) {
        MaterialListItem(
            text = { Text(name, maxLines = 1) },
            secondaryText = { Text(artist) },
            icon = {
                PlainListIconBackground {
                    Icon(Icons.Rounded.MusicNote, null)
                }
            },
            modifier = Modifier.clickable(onClick = onClick)
        )
    }
}