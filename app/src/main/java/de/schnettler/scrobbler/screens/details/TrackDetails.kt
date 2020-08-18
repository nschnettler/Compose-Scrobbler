package de.schnettler.scrobbler.screens.details

import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.EntityWithStatsAndInfo.TrackWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.components.ExpandingInfoCard
import de.schnettler.scrobbler.components.ListeningStats

@Composable
fun TrackDetailScreen(
    trackDetails: TrackWithStatsAndInfo,
    onTagClicked: (String) -> Unit,
    onAlbumClicked: (LastFmEntity) -> Unit
) {
    val (track, stats, info, album) = trackDetails
    Stack(Modifier.fillMaxSize()) {
        ScrollableColumn(children = {
            AlbumCategory(
                album = album,
                artistPlaceholder = track.artist,
                onAlbumSelected = onAlbumClicked
            )
            ExpandingInfoCard(info = info?.wiki)
            ListeningStats(item = stats)
            if (info?.tags?.isNotEmpty() == true) {
                TagCategory(tags = info.tags, onTagClicked = onTagClicked)
            }
        })
        FloatingActionButton(
            onClick = { },
            Modifier.gravity(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp)
        ) {
            if (info?.loved == true) {
                Icon(asset = Icons.Rounded.Favorite)
            } else {
                Icon(asset = Icons.Rounded.FavoriteBorder)
            }
        }
    }
}