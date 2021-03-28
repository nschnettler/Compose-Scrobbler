package de.schnettler.scrobbler.ui.detail.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.schnettler.common.whenNotEmpty
import de.schnettler.database.models.EntityWithStats.TrackWithStats
import de.schnettler.database.models.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.scrobbler.ui.common.compose.DominantColorCache
import de.schnettler.scrobbler.ui.common.compose.itemSpacer
import de.schnettler.scrobbler.ui.common.compose.navigation.MenuAction
import de.schnettler.scrobbler.ui.common.compose.navigation.UIAction
import de.schnettler.scrobbler.ui.common.compose.navigation.UIAction.ListingSelected
import de.schnettler.scrobbler.ui.common.compose.rememberDominantColorCache
import de.schnettler.scrobbler.ui.common.compose.widget.Carousel
import de.schnettler.scrobbler.ui.common.compose.widget.CollapsingToolbar
import de.schnettler.scrobbler.ui.common.compose.widget.Header
import de.schnettler.scrobbler.ui.common.compose.widget.ListeningStats
import de.schnettler.scrobbler.ui.common.compose.widget.MediaCard
import de.schnettler.scrobbler.ui.common.compose.widget.PlainListIconBackground
import de.schnettler.scrobbler.ui.common.util.abbreviate
import de.schnettler.scrobbler.ui.detail.R
import de.schnettler.scrobbler.ui.detail.widget.ChipRow
import de.schnettler.scrobbler.ui.detail.widget.ExpandingInfoCard
import com.google.accompanist.insets.navigationBarsHeight

@Composable
fun ArtistDetailScreen(
    info: ArtistWithStatsAndInfo,
    actioner: (UIAction) -> Unit
) {
    val colorCache = rememberDominantColorCache()
    CollapsingToolbar(
        imageUrl = info.entity.imageUrl,
        title = info.entity.name,
        actioner = actioner,
        menuActions = listOf(MenuAction.OpenInBrowser(info.entity.url))
    ) {
        detailItems(artistInfo = info, dominantColorCache = colorCache, actioner = actioner)
    }
}

private fun LazyListScope.detailItems(
    artistInfo: ArtistWithStatsAndInfo,
    dominantColorCache: DominantColorCache,
    actioner: (UIAction) -> Unit
) {
    val (_, stats, info) = artistInfo
    itemSpacer(16.dp)

    // BIO
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

    // Tracks
    artistInfo.topTracks.whenNotEmpty { tracks ->
        itemSpacer(16.dp)
        item { Header(title = stringResource(id = R.string.header_toptracks)) }
        itemsIndexed(tracks) { index, track ->
            TrackItem(index = index, track = track, actionHandler = actioner)
        }
    }

    itemSpacer(16.dp)

    // Albums
    item {
        Carousel(items = artistInfo.topAlbums, titleRes = R.string.header_topalbums) { (album, stats) ->
            MediaCard(
                name = album.name,
                plays = stats.plays,
                imageUrl = album.imageUrl,
                modifier = Modifier.size(256.dp),
                colorCache = dominantColorCache
            ) { actioner(ListingSelected(album)) }
        }
    }

    itemSpacer(16.dp)

    // Artists
    item {
        Carousel(items = artistInfo.similarArtists, titleRes = R.string.artist_similar) { artist ->
            MediaCard(
                name = artist.name,
                imageUrl = artist.imageUrl,
                modifier = Modifier.size(180.dp),
                colorCache = dominantColorCache
            ) {
                actioner(ListingSelected(artist))
            }
        }
    }

    item {
        Spacer(modifier = Modifier.navigationBarsHeight(16.dp))
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TrackItem(index: Int, track: TrackWithStats, actionHandler: (UIAction) -> Unit) {
    ListItem(
        text = { Text(track.entity.name) },
        secondaryText = {
            Text("${track.stats.listeners.abbreviate()} ${stringResource(id = R.string.stats_listeners)}")
        },
        icon = { PlainListIconBackground { Text(text = "${index + 1}") } },
        modifier = Modifier.clickable(onClick = { actionHandler(ListingSelected(track.entity)) })
    )
}