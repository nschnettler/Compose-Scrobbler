package de.schnettler.scrobbler.details.ui.track

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import de.schnettler.scrobbler.compose.ktx.itemSpacer
import de.schnettler.scrobbler.compose.navigation.UIAction
import de.schnettler.scrobbler.compose.widget.ChipRow
import de.schnettler.scrobbler.compose.widget.Header
import de.schnettler.scrobbler.core.ktx.whenNotEmpty
import de.schnettler.scrobbler.details.R
import de.schnettler.scrobbler.details.model.TrackDetailEntity
import de.schnettler.scrobbler.details.ui.AlbumCategory
import de.schnettler.scrobbler.details.ui.widget.ExpandingInfoCard
import de.schnettler.scrobbler.details.ui.widget.ListeningStats

@Composable
fun TrackDetailScreen(
    viewModel: TrackViewModel,
    details: TrackDetailEntity,
    actioner: (UIAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (track, stats, info, album) = details
    Box(modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = WindowInsets.systemBars.asPaddingValues()
        ) {
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

            itemSpacer(24.dp)
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