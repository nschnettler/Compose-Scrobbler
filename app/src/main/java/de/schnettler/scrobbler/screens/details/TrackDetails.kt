package de.schnettler.scrobbler.screens.details

import androidx.compose.Composable
import androidx.ui.foundation.ScrollableColumn
import de.schnettler.database.models.EntityWithStatsAndInfo.TrackWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.components.ListeningStats

@Composable
fun TrackDetailScreen(
    trackDetails: TrackWithStatsAndInfo,
    onTagClicked: (String) -> Unit,
    onAlbumClicked: (LastFmEntity) -> Unit
) {
    val (track, stats, info, album) = trackDetails
    ScrollableColumn(children = {
        AlbumCategory(
            album = album,
            artistPlaceholder = track.artist,
            onAlbumSelected = onAlbumClicked
        )
        AlbumDescription(description = info?.wiki)
        ListeningStats(item = stats)
        if (info?.tags?.isNotEmpty() == true) {
            TagCategory(tags = info.tags, onTagClicked = onTagClicked)
        }
    })
}