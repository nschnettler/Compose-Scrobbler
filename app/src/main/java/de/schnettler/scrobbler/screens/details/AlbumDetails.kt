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
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.EntityWithStatsAndInfo.AlbumWithStatsAndInfo
import de.schnettler.scrobbler.UIAction
import de.schnettler.scrobbler.UIAction.ListingSelected
import de.schnettler.scrobbler.components.CollapsingToolbar
import de.schnettler.scrobbler.components.ExpandingInfoCard
import de.schnettler.scrobbler.components.IndexListIconBackground
import de.schnettler.scrobbler.components.ListTitle
import de.schnettler.scrobbler.components.ListeningStats
import de.schnettler.scrobbler.components.NameListIcon
import de.schnettler.scrobbler.components.Spacer
import de.schnettler.scrobbler.screens.TagCategory
import de.schnettler.scrobbler.util.fromHtmlLastFm
import de.schnettler.scrobbler.util.navigationBarsHeightPlus

@OptIn(ExperimentalLayout::class, ExperimentalLazyDsl::class)
@Composable
fun AlbumDetailScreen(
    albumDetails: AlbumWithStatsAndInfo,
    actionHandler: (UIAction) -> Unit,
) {
    val (album, stats, info) = albumDetails
    CollapsingToolbar(imageUrl = album.imageUrl, title = album.name, statusBarGuardAlpha = 0F, onUp = {
        actionHandler(UIAction.NavigateUp)
    }) {
        ListItem(
            text = { Text(text = album.artist) },
            icon = {
                NameListIcon(title = album.artist)
            }
        )
        ExpandingInfoCard(info?.wiki?.fromHtmlLastFm())

        Spacer(size = 16.dp)

        ListeningStats(item = stats)

        Spacer(size = 16.dp)

        TagCategory(tags = albumDetails.info?.tags ?: emptyList(), actionHandler = actionHandler)

        Spacer(modifier = Modifier.preferredHeight(16.dp))

        ListTitle(title = "Tracks")

        albumDetails.tracks.forEachIndexed { index, (track, _) ->
            ListItem(
                text = { Text(track.name) },
                icon = { IndexListIconBackground(index = index) },
                modifier = Modifier.clickable(onClick = { actionHandler(ListingSelected(track)) })
            )
        }

        Spacer(modifier = Modifier.navigationBarsHeightPlus(8.dp))
    }
}