package de.schnettler.scrobbler.screens.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.EntityWithStats.TrackWithStats
import de.schnettler.database.models.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.ui.common.compose.Carousel
import de.schnettler.scrobbler.components.ChipRow
import de.schnettler.scrobbler.components.CollapsingToolbar
import de.schnettler.scrobbler.components.ExpandingInfoCard
import de.schnettler.scrobbler.components.ListWithTitle
import de.schnettler.scrobbler.ui.common.compose.ListeningStats
import de.schnettler.scrobbler.ui.common.compose.MediaCard
import de.schnettler.scrobbler.ui.common.compose.rememberDominantColorCache
import de.schnettler.scrobbler.ui.common.compose.PlainListIconBackground
import de.schnettler.scrobbler.ui.common.compose.UIAction
import de.schnettler.scrobbler.ui.common.compose.UIAction.ListingSelected
import de.schnettler.scrobbler.ui.common.util.abbreviate
import de.schnettler.scrobbler.util.MenuAction
import dev.chrisbanes.accompanist.insets.navigationBarsHeight

@Composable
fun ArtistDetailScreen(
    info: ArtistWithStatsAndInfo,
    actioner: (UIAction) -> Unit
) {
    CollapsingToolbar(
        imageUrl = info.entity.imageUrl,
        title = info.entity.name,
        actionHandler = actioner,
        statusBarGuardAlpha = 0f,
        menuActions = listOf(MenuAction.OpenInBrowser(info.entity.url))
    ) {
        Content(artistInfo = info, actioner = actioner)
    }
}

@Composable
fun Content(artistInfo: ArtistWithStatsAndInfo, actioner: (UIAction) -> Unit) {
    val (_, stats, info) = artistInfo
    val colorCache = rememberDominantColorCache()

    // BIO
    ExpandingInfoCard(info = info?.wiki)

    // Stats
    ListeningStats(item = stats)

    // Tags
    ListWithTitle(title = stringResource(id = R.string.header_tags), list = info?.tags) { tags ->
        ChipRow(items = tags, onChipClicked = { actioner(UIAction.TagSelected(it)) })
    }

    // Tracks
    ListWithTitle(title = stringResource(id = R.string.header_toptracks), list = artistInfo.topTracks) { tracks ->
        TrackListWithStats(tracks = tracks, actionHandler = actioner)
    }

    // Albums
    Carousel(items = artistInfo.topAlbums, titleRes = R.string.header_topalbums) { (album, stats), padding ->
        MediaCard(
            name = album.name,
            plays = stats.plays,
            imageUrl = album.imageUrl,
            modifier = Modifier.padding(padding).preferredSize(256.dp),
            colorCache = colorCache
        ) { actioner(ListingSelected(album)) }
    }

    // Artists
    Carousel(items = artistInfo.similarArtists, titleRes = R.string.artist_similar) { artist, padding ->
        MediaCard(
            name = artist.name,
            imageUrl = artist.imageUrl,
            modifier = Modifier.padding(padding).preferredSize(180.dp),
            colorCache = colorCache
        ) {
            actioner(ListingSelected(artist))
        }
    }

    Spacer(modifier = Modifier.preferredHeight(8.dp))
    Spacer(modifier = Modifier.navigationBarsHeight())
}

@Composable
fun TrackListWithStats(tracks: List<TrackWithStats>, actionHandler: (UIAction) -> Unit) {
    tracks.forEachIndexed { index, (track, stats) ->
        ListItem(
            text = { Text(track.name) },
            secondaryText = {
                Text("${stats.listeners.abbreviate()} ${stringResource(id = R.string.stats_listeners)}")
            },
            icon = { PlainListIconBackground { Text(text = "${index + 1}") } },
            modifier = Modifier.clickable(onClick = { actionHandler(ListingSelected(track)) })
        )
    }
}