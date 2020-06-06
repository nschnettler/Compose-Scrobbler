package de.schnettler.scrobbler.screens.details

import androidx.compose.Composable
import androidx.ui.foundation.VerticalScroller
import de.schnettler.database.models.TrackDomain

@Composable
fun TrackDetailScreen(track: TrackDomain) {
    VerticalScroller() {
        track.album?.let { album ->
            AlbumCategory(
                album
            )
        }
        StatsRow(item = track)
        if (track.tags.isNotEmpty()) {
            TagCategory(tags = track.tags)
        }
    }
}