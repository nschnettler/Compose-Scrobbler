package de.schnettler.scrobbler.screens.details

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
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
import dev.chrisbanes.accompanist.insets.navigationBarsHeight
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.statusBarsHeight

@Composable
fun TrackDetailScreen(
    details: TrackWithStatsAndInfo,
    actioner: (UIAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (track, stats, info, album) = details
    Box(modifier.fillMaxSize()) {
        ScrollableColumn(content = {
            Spacer(modifier = Modifier.statusBarsHeight())
            AlbumCategory(
                album = album,
                artistPlaceholder = track.artist,
                actionHandler = actioner
            )
            ExpandingInfoCard(info = info?.wiki)
            ListeningStats(item = stats)
            if (info?.tags?.isNotEmpty() == true) {
                TagCategory(tags = info.tags, actionHandler = actioner)
            }
            Spacer(modifier = Modifier.preferredHeight(8.dp))
            Spacer(modifier = Modifier.navigationBarsHeight())
        })
        info?.let {
            FloatingActionButton(
                onClick = {
                    actioner(UIAction.TrackLiked(track, info.copy(loved = !info.loved)))
                },
                Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp).navigationBarsPadding()
            ) {
                if (info.loved) {
                    Icon(Icons.Rounded.Favorite)
                } else {
                    Icon(imageVector = Icons.Rounded.FavoriteBorder)
                }
            }
        }
    }
}