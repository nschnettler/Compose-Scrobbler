package de.schnettler.scrobbler.screens.details

import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Spacer
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
import de.schnettler.scrobbler.UIAction
import de.schnettler.scrobbler.components.ExpandingInfoCard
import de.schnettler.scrobbler.components.ListeningStats
import de.schnettler.scrobbler.screens.AlbumCategory
import de.schnettler.scrobbler.screens.TagCategory
import de.schnettler.scrobbler.util.navigationBarsHeightPlus
import de.schnettler.scrobbler.util.navigationBarsPadding
import de.schnettler.scrobbler.util.statusBarsHeight

@Composable
fun TrackDetailScreen(
    trackDetails: TrackWithStatsAndInfo,
    actionHandler: (UIAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (track, stats, info, album) = trackDetails
    Stack(modifier.fillMaxSize()) {
        ScrollableColumn(children = {
            Spacer(modifier = Modifier.statusBarsHeight())
            AlbumCategory(
                album = album,
                artistPlaceholder = track.artist,
                actionHandler = actionHandler
            )
            ExpandingInfoCard(info = info?.wiki)
            ListeningStats(item = stats)
            if (info?.tags?.isNotEmpty() == true) {
                TagCategory(tags = info.tags, actionHandler = actionHandler)
            }
            Spacer(modifier = Modifier.navigationBarsHeightPlus(8.dp))
        })
        info?.let {
            FloatingActionButton(
                onClick = {
                    actionHandler(UIAction.TrackLiked(track, info.copy(loved = !info.loved)))
                },
                Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp).navigationBarsPadding()
            ) {
                if (info.loved) {
                    Icon(asset = Icons.Rounded.Favorite)
                } else {
                    Icon(asset = Icons.Rounded.FavoriteBorder)
                }
            }
        }
    }
}