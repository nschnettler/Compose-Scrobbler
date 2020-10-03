package de.schnettler.scrobbler.screens.details

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.EntityWithStats.TrackWithStats
import de.schnettler.database.models.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.UIAction
import de.schnettler.scrobbler.UIAction.ListingSelected
import de.schnettler.scrobbler.components.CollapsingToolbar
import de.schnettler.scrobbler.components.ExpandingInfoCard
import de.schnettler.scrobbler.components.ListTitle
import de.schnettler.scrobbler.components.ListeningStats
import de.schnettler.scrobbler.components.ListingScroller
import de.schnettler.scrobbler.components.PlainListIconBackground
import de.schnettler.scrobbler.screens.TagCategory
import de.schnettler.scrobbler.util.PlaysStyle
import de.schnettler.scrobbler.util.abbreviate
import de.schnettler.scrobbler.util.navigationBarsHeightPlus

@Composable
fun ArtistDetailScreen(
    artistInfo: ArtistWithStatsAndInfo,
    actionHandler: (UIAction) -> Unit
) {
    CollapsingToolbar(
        imageUrl = artistInfo.entity.imageUrl,
        title = artistInfo.entity.name,
        actionHandler = actionHandler,
        statusBarGuardAlpha = 0f
    ) {
        Content(artistInfo = artistInfo, actionHandler = actionHandler)
    }
}

@Composable
fun Content(artistInfo: ArtistWithStatsAndInfo, actionHandler: (UIAction) -> Unit) {
    val (_, stats, info) = artistInfo

    ExpandingInfoCard(info = info?.wiki)
    ListeningStats(item = stats)
    info?.tags?.let { TagCategory(tags = it, actionHandler = actionHandler) }
    ListTitle(title = stringResource(id = R.string.header_toptracks))
    TrackListWithStats(tracks = artistInfo.topTracks, actionHandler = actionHandler)

    ListingScroller(
        title = stringResource(id = R.string.header_topalbums),
        content = artistInfo.topAlbums,
        height = 160.dp,
        playsStyle = PlaysStyle.PUBLIC_PLAYS,
        actionHandler = actionHandler
    )

    ListingScroller(
        title = stringResource(id = R.string.artist_similar),
        content = artistInfo.similarArtists,
        height = 136.dp,
        playsStyle = PlaysStyle.NO_PLAYS,
        actionHandler = actionHandler
    )

    Spacer(modifier = Modifier.navigationBarsHeightPlus(8.dp))
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