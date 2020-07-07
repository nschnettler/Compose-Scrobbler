package de.schnettler.scrobbler.screens.details

import androidx.compose.Composable
import androidx.ui.foundation.VerticalScroller
import de.schnettler.database.models.TrackDomain
import de.schnettler.scrobbler.components.ListeningStats

@Composable
fun TrackDetailScreen(track: TrackDomain) {
    VerticalScroller() {
        track.album?.let { album ->
            AlbumCategory(
                album
            )
        }
        ListeningStats(item = track)
        if (track.tags.isNotEmpty()) {
            TagCategory(tags = track.tags)
        }
    }
}