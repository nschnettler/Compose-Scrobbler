package de.schnettler.scrobbler.screens.details

import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.EntityWithStatsAndInfo.AlbumWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.UIAction
import de.schnettler.scrobbler.UIAction.ListingSelected
import de.schnettler.scrobbler.UIAction.TagSelected
import de.schnettler.scrobbler.components.ChipRow
import de.schnettler.scrobbler.components.ExpandingInfoCard
import de.schnettler.scrobbler.components.IndexListIconBackground
import de.schnettler.scrobbler.components.ListeningStats
import de.schnettler.scrobbler.theme.AppColor
import de.schnettler.scrobbler.util.fromHtmlLastFm
import de.schnettler.scrobbler.util.navigationBarsHeightPlus
import de.schnettler.scrobbler.util.statusBarsHeight
import dev.chrisbanes.accompanist.coil.CoilImage

@OptIn(ExperimentalLayout::class, ExperimentalLazyDsl::class)
@Composable
fun AlbumDetailScreen(
    albumDetails: AlbumWithStatsAndInfo,
    actionHandler: (UIAction) -> Unit,
) {
    val (album, stats, info) = albumDetails
    LazyColumn {
        item { Spacer(modifier = Modifier.statusBarsHeight()) }
        item {
            Row(modifier = Modifier.padding(16.dp)) {
                AlbumArtwork(url = album.imageUrl)
                Spacer(modifier = Modifier.preferredWidth(16.dp))
                AlbumInfo(
                    name = album.name,
                    artist = album.artist,
                    tracks = albumDetails.tracks.size,
                    duration = albumDetails.getLength(),
                    onArtistSelected = { actionHandler(ListingSelected(it)) }
                )
            }
        }
        item {
            albumDetails.info?.tags?.let {
                ChipRow(items = it, onChipClicked = { tag ->
                    actionHandler(TagSelected(tag))
                })
            }
        }
        item { Spacer(modifier = Modifier.preferredHeight(16.dp)) }
        item { ListeningStats(item = stats) }
        item { ExpandingInfoCard(info?.wiki?.fromHtmlLastFm()) }
        item { Spacer(modifier = Modifier.preferredHeight(16.dp)) }
        // TODO: Check if itemsIndexed works with empty lists now
        if (albumDetails.tracks.isNotEmpty()) {
            itemsIndexed(albumDetails.tracks) { index, (track, _) ->
                ListItem(
                    text = { Text(track.name) },
                    icon = { IndexListIconBackground(index = index) },
                    modifier = Modifier.clickable(onClick = { actionHandler(ListingSelected(track)) })
                )
            }
        }
        item { Spacer(modifier = Modifier.navigationBarsHeightPlus(8.dp)) }
    }
}

@Composable
fun AlbumArtwork(url: String?) {
    Card(
        modifier = Modifier.preferredWidth(182.dp).aspectRatio(1F)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(AppColor.BackgroundElevated)
        ) {
            url?.let { url ->
                CoilImage(data = url, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun AlbumInfo(
    name: String,
    artist: String,
    tracks: Int,
    duration: Long,
    onArtistSelected: (LastFmEntity.Artist) -> Unit
) {
    Column(Modifier.padding(top = 8.dp)) {
        Text(
            text = name,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.h5
        )
        Text(
            text = "${stringResource(id = R.string.albumdetails_by)} $artist",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.clickable(onClick = {
                onArtistSelected(LastFmEntity.Artist(name = artist, url = ""))
            })
        )
        Text(
            text = "$tracks ${stringResource(id = R.string.albumdetails_tracks)} ⦁ " +
                    "$duration ${stringResource(id = R.string.albumdetails_minutes)}",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.subtitle2
        )
    }
}