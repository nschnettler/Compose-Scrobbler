package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.ui.foundation.Text
import de.schnettler.database.models.TopListEntry

@Composable
fun DetailScreen(item: TopListEntry) {
    Text(text = item.title)
}