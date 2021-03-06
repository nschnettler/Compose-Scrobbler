package de.schnettler.scrobbler.ui.detail.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import de.schnettler.scrobbler.ui.common.compose.navigation.UIAction
import de.schnettler.scrobbler.ui.common.compose.widget.ListeningStats
import de.schnettler.scrobbler.ui.detail.AlbumCategory
import de.schnettler.scrobbler.ui.detail.TagCategory
import de.schnettler.scrobbler.ui.detail.widget.ExpandingInfoCard
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
        LazyColumn {
            item { Spacer(modifier = Modifier.statusBarsHeight()) }
            item {
                AlbumCategory(
                    album = album,
                    artistPlaceholder = track.artist,
                    actionHandler = actioner
                )
            }
            item { ExpandingInfoCard(info = info?.wiki) }
            item { ListeningStats(item = stats) }
            item {
                if (info?.tags?.isNotEmpty() == true) {
                    TagCategory(tags = info.tags, actionHandler = actioner)
                }
            }
            item {
                Spacer(modifier = Modifier.navigationBarsHeight(8.dp))
            }
        }
        info?.let {
            FloatingActionButton(
                onClick = {
                    actioner(UIAction.TrackLiked(track, info.copy(loved = !info.loved)))
                },
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
                    .navigationBarsPadding()
            ) {
                if (info.loved) {
                    Icon(Icons.Rounded.Favorite, null)
                } else {
                    Icon(imageVector = Icons.Rounded.FavoriteBorder, null)
                }
            }
        }
    }
}