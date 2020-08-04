package de.schnettler.scrobbler.screens.details

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.getValue
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.ExperimentalLayout
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredWidth
import androidx.ui.material.ListItem
import androidx.ui.material.Surface
import androidx.ui.res.colorResource
import androidx.ui.unit.dp
import de.schnettler.database.models.EntityWithStatsAndInfo.AlbumWithStatsAndInfo
import de.schnettler.database.models.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.database.models.EntityWithStatsAndInfo.TrackWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.ChipRow
import de.schnettler.scrobbler.components.LiveDataLoadingComponent
import de.schnettler.scrobbler.components.SwipeRefreshPrograssIndicator
import de.schnettler.scrobbler.components.SwipeToRefreshLayout
import de.schnettler.scrobbler.components.TitleComponent
import de.schnettler.scrobbler.viewmodels.DetailViewModel
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade

@Composable
fun DetailScreen(
    model: DetailViewModel,
    onListingSelected: (LastFmEntity) -> Unit,
    onTagClicked: (String) -> Unit
) {
    val artistState by model.state.collectAsState()

    if (artistState.isLoading) {
        LiveDataLoadingComponent()
    } else {
        SwipeToRefreshLayout(
            refreshingState = artistState.isRefreshing,
            onRefresh = { model.refresh() },
            refreshIndicator = { SwipeRefreshPrograssIndicator() }
        ) {
            artistState.currentData.let { details ->
                when (details) {
                    is ArtistWithStatsAndInfo -> ArtistDetailScreen(
                        artistInfo = details,
                        onListingSelected = onListingSelected,
                        onTagClicked = onTagClicked
                    )
                    is TrackWithStatsAndInfo -> TrackDetailScreen(
                        details,
                        onTagClicked = onTagClicked,
                        onListingSelected
                    )
                    is AlbumWithStatsAndInfo -> AlbumDetailScreen(
                        albumDetails = details,
                        onListingSelected = onListingSelected,
                        onTagClicked = onTagClicked
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayout::class)
@Composable
fun TagCategory(tags: List<String>, onTagClicked: (String) -> Unit) {
    TitleComponent(title = "Tags")
    ChipRow(items = tags, onChipClicked = onTagClicked)
}

@Composable
fun AlbumCategory(
    album: LastFmEntity.Album?,
    artistPlaceholder: String,
    onAlbumSelected: (LastFmEntity) -> Unit
) {
    TitleComponent(title = "Aus dem Album")
    ListItem(
        text = {
            Text(album?.name ?: "Unknown Album")
        },
        secondaryText = {
            Text(album?.artist ?: artistPlaceholder)
        },
        icon = {
            Surface(
                color = colorResource(id = R.color.colorStroke),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(modifier = Modifier.preferredHeight(60.dp) + Modifier.preferredWidth(60.dp)) {
                    album?.imageUrl?.let {
                        CoilImageWithCrossfade(
                            data = it,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        },
        onClick = {
            onAlbumSelected(album ?: LastFmEntity.Artist(name = artistPlaceholder, url = ""))
        }
    )
}