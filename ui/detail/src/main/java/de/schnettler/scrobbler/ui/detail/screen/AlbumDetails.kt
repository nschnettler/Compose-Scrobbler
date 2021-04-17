package de.schnettler.scrobbler.ui.detail.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.CoilImage
import com.google.accompanist.insets.navigationBarsHeight
import de.schnettler.common.whenNotEmpty
import de.schnettler.scrobbler.compose.navigation.MenuAction
import de.schnettler.scrobbler.compose.navigation.UIAction
import de.schnettler.scrobbler.compose.navigation.UIAction.ListingSelected
import de.schnettler.scrobbler.compose.widget.ChipRow
import de.schnettler.scrobbler.compose.widget.CollapsingToolbar
import de.schnettler.scrobbler.compose.widget.Header
import de.schnettler.scrobbler.compose.widget.IndexListIconBackground
import de.schnettler.scrobbler.compose.widget.PlainListIconBackground
import de.schnettler.scrobbler.core.ktx.fromHtmlLastFm
import de.schnettler.scrobbler.model.EntityWithStatsAndInfo.AlbumDetails
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.ui.common.compose.itemSpacer
import de.schnettler.scrobbler.ui.common.util.asMinSec
import de.schnettler.scrobbler.ui.detail.R
import de.schnettler.scrobbler.ui.detail.widget.ExpandingInfoCard
import de.schnettler.scrobbler.ui.detail.widget.ListeningStats
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalMaterialApi::class)
@Composable
fun AlbumDetailScreen(
    details: AlbumDetails,
    actioner: (UIAction) -> Unit,
) {
    val (album, stats, info, artist) = details
    CollapsingToolbar(
        title = album.name,
        imageUrl = album.imageUrl,
        actioner = actioner,
        menuActions = listOf(MenuAction.OpenInBrowser(album.url))
    ) {
        // Album Info
        item {
            ArtistItem(
                artist = artist ?: LastFmEntity.Artist(album.artist, ""),
                details.trackNumber,
                details.runtime,
                actioner
            )
        }

        itemSpacer(16.dp)

        // Info
        item { ExpandingInfoCard(info?.wiki?.fromHtmlLastFm()) }

        itemSpacer(24.dp)

        // Stats
        item { ListeningStats(item = stats) }

        // Tags
        details.info?.tags?.whenNotEmpty { tags ->
            itemSpacer(16.dp)
            item { Header(title = stringResource(id = R.string.header_tags)) }
            item { ChipRow(items = tags, onChipClicked = { actioner(UIAction.TagSelected(it)) }) }
        }

        // Tracks
        details.tracks.whenNotEmpty { tracks ->
            itemSpacer(16.dp)
            item { Header(title = "Tracks") }
            itemsIndexed(tracks) { index, (track, info) ->
                ListItem(
                    text = { Text(track.name) },
                    secondaryText = { Text(text = info.duration.asMinSec()) },
                    icon = { IndexListIconBackground(index = index) },
                    modifier = Modifier.clickable(onClick = { actioner(ListingSelected(track)) })
                )
            }
        }

        item { Spacer(modifier = Modifier.navigationBarsHeight(16.dp)) }
    }
}

@OptIn(ExperimentalTime::class, ExperimentalMaterialApi::class)
@Composable
private fun ArtistItem(
    artist: LastFmEntity.Artist,
    trackNumber: Int,
    albumLength: Duration,
    actionHandler: (UIAction) -> Unit
) {
    ListItem(
        text = { Text(text = artist.name) },
        secondaryText = {
            Text(
                text = "$trackNumber ${stringResource(id = R.string.albumdetails_tracks)} ⦁ " +
                        "${albumLength.inMinutes.roundToInt()} ${stringResource(id = R.string.albumdetails_minutes)}"
            )
        },
        icon = { PlainListIconBackground { CoilImage(data = artist.imageUrl ?: "", contentDescription = null) } },
        modifier = Modifier.clickable(onClick = { actionHandler(ListingSelected(artist)) })
    )
}