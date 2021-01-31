package de.schnettler.scrobbler.ui.detail.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.EntityWithStatsAndInfo.AlbumDetails
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.ui.common.compose.navigation.MenuAction
import de.schnettler.scrobbler.ui.common.compose.navigation.UIAction
import de.schnettler.scrobbler.ui.common.compose.navigation.UIAction.ListingSelected
import de.schnettler.scrobbler.ui.common.compose.widget.CollapsingToolbar
import de.schnettler.scrobbler.ui.common.compose.widget.IndexListIconBackground
import de.schnettler.scrobbler.ui.common.compose.widget.ListeningStats
import de.schnettler.scrobbler.ui.common.compose.widget.PlainListIconBackground
import de.schnettler.scrobbler.ui.common.compose.widget.Spacer
import de.schnettler.scrobbler.ui.common.util.asMinSec
import de.schnettler.scrobbler.ui.common.util.fromHtmlLastFm
import de.schnettler.scrobbler.ui.detail.R
import de.schnettler.scrobbler.ui.detail.widget.ChipRow
import de.schnettler.scrobbler.ui.detail.widget.ExpandingInfoCard
import de.schnettler.scrobbler.ui.detail.widget.ListWithTitle
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.insets.navigationBarsHeight
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalLayout::class, ExperimentalTime::class)
@Composable
fun AlbumDetailScreen(
    details: AlbumDetails,
    actioner: (UIAction) -> Unit,
) {
    val (album, stats, info, artist) = details
    CollapsingToolbar(
        imageUrl = album.imageUrl,
        title = album.name,
        statusBarGuardAlpha = 0F,
        actionHandler = actioner,
        menuActions = listOf(MenuAction.OpenInBrowser(album.url))
    ) {
        ArtistItem(
            artist = artist ?: LastFmEntity.Artist(album.artist, ""),
            details.trackNumber,
            details.runtime,
            actioner
        )

        ExpandingInfoCard(info?.wiki?.fromHtmlLastFm())

        Spacer(size = 16.dp)

        ListeningStats(item = stats)

        ListWithTitle(title = stringResource(id = R.string.header_tags), list = details.info?.tags) { tags ->
            ChipRow(items = tags, onChipClicked = { actioner(UIAction.TagSelected(it)) })
        }

        Spacer(modifier = Modifier.preferredHeight(16.dp))

        ListWithTitle(title = "Tracks", list = details.tracks) { tracks ->
            tracks.forEachIndexed { index, (track, info) ->
                ListItem(
                    text = { Text(track.name) },
                    secondaryText = { Text(text = info.duration.asMinSec()) },
                    icon = { IndexListIconBackground(index = index) },
                    modifier = Modifier.clickable(onClick = { actioner(ListingSelected(track)) })
                )
            }
        }

        Spacer(modifier = Modifier.preferredHeight(8.dp))
        Spacer(modifier = Modifier.navigationBarsHeight())
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