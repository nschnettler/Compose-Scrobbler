package de.schnettler.scrobbler.screens.details

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.material.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.EntityWithStatsAndInfo.AlbumDetails
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.UIAction
import de.schnettler.scrobbler.UIAction.ListingSelected
import de.schnettler.scrobbler.components.ChipRow
import de.schnettler.scrobbler.components.CollapsingToolbar
import de.schnettler.scrobbler.components.ExpandingInfoCard
import de.schnettler.scrobbler.components.IndexListIconBackground
import de.schnettler.scrobbler.components.ListTitle
import de.schnettler.scrobbler.components.ListeningStats
import de.schnettler.scrobbler.components.PlainListIconBackground
import de.schnettler.scrobbler.components.Spacer
import de.schnettler.scrobbler.util.MenuAction
import de.schnettler.scrobbler.util.asMinSec
import de.schnettler.scrobbler.util.fromHtmlLastFm
import de.schnettler.scrobbler.util.navigationBarsHeightPlus
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalLayout::class, ExperimentalLazyDsl::class, ExperimentalTime::class)
@Composable
fun AlbumDetailScreen(
    albumDetails: AlbumDetails,
    actionHandler: (UIAction) -> Unit,
) {
    val (album, stats, info, artist) = albumDetails
    CollapsingToolbar(
        imageUrl = album.imageUrl,
        title = album.name,
        statusBarGuardAlpha = 0F,
        actionHandler = actionHandler,
        menuActions = listOf(MenuAction.OpenInBrowser(album.url))
    ) {
        ArtistItem(
            artist = artist ?: LastFmEntity.Artist(album.artist, ""),
            albumDetails.trackNumber,
            albumDetails.runtime,
            actionHandler
        )

        ExpandingInfoCard(info?.wiki?.fromHtmlLastFm())

        Spacer(size = 16.dp)

        ListeningStats(item = stats)

        ListWithTitle(title = stringResource(id = R.string.header_tags), list = albumDetails.info?.tags) { tags ->
            ChipRow(items = tags, onChipClicked = { actionHandler(UIAction.TagSelected(it)) })
        }

        Spacer(modifier = Modifier.preferredHeight(16.dp))

        ListWithTitle(title = "Tracks", list = albumDetails.tracks) { tracks ->
            tracks.forEachIndexed { index, (track, info) ->
                ListItem(
                    text = { Text(track.name) },
                    secondaryText = { Text(text = info.duration.asMinSec()) },
                    icon = { IndexListIconBackground(index = index) },
                    modifier = Modifier.clickable(onClick = { actionHandler(ListingSelected(track)) })
                )
            }
        }

        Spacer(modifier = Modifier.navigationBarsHeightPlus(8.dp))
    }
}

@OptIn(ExperimentalTime::class)
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
        icon = { PlainListIconBackground { CoilImage(data = artist.imageUrl ?: "") } },
        modifier = Modifier.clickable(onClick = { actionHandler(ListingSelected(artist)) })
    )
}

@Composable
fun <T> ListWithTitle(title: String, list: List<T>?, content: @Composable (List<T>) -> Unit) {
    if (list?.isNotEmpty() == true) {
        ListTitle(title = title)
        content(list)
    }
}