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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsHeight
import de.schnettler.common.whenNotEmpty
import de.schnettler.scrobbler.compose.navigation.UIAction
import de.schnettler.scrobbler.compose.widget.ChipRow
import de.schnettler.scrobbler.compose.widget.Header
import de.schnettler.scrobbler.model.EntityWithStatsAndInfo.TrackWithStatsAndInfo
import de.schnettler.scrobbler.ui.common.compose.itemSpacer
import de.schnettler.scrobbler.ui.detail.AlbumCategory
import de.schnettler.scrobbler.ui.detail.R
import de.schnettler.scrobbler.ui.detail.viewmodel.TrackViewModel
import de.schnettler.scrobbler.ui.detail.widget.ExpandingInfoCard
import de.schnettler.scrobbler.ui.detail.widget.ListeningStats

@Composable
fun TrackDetailScreen(
    viewModel: TrackViewModel,
    details: TrackWithStatsAndInfo,
    actioner: (UIAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (track, stats, info, album) = details
    Box(modifier.fillMaxSize()) {
        LazyColumn {
            item { Spacer(modifier = Modifier.statusBarsHeight()) }

            // Album
            item {
                AlbumCategory(
                    album = album,
                    artistPlaceholder = track.artist,
                    actionHandler = actioner
                )
            }

            itemSpacer(16.dp)

            // Info
            item { ExpandingInfoCard(info = info?.wiki) }

            itemSpacer(24.dp)

            // Stats
            item { ListeningStats(item = stats) }

            // Tags
            info?.tags?.whenNotEmpty { tags ->
                itemSpacer(16.dp)
                item { Header(title = stringResource(id = R.string.header_tags)) }
                item { ChipRow(items = tags, onChipClicked = { actioner(UIAction.TagSelected(it)) }) }
            }

            item {
                Spacer(modifier = Modifier.navigationBarsHeight(16.dp))
            }
        }
        info?.let {
            FloatingActionButton(
                onClick = { viewModel.onToggleLoveTrackClicked(track, info.copy(loved = !info.loved)) },
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