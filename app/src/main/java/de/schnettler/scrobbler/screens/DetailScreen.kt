package de.schnettler.scrobbler.screens

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ListItem
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.EntityWithStatsAndInfo.AlbumDetails
import de.schnettler.database.models.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.database.models.EntityWithStatsAndInfo.TrackWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.UIAction
import de.schnettler.scrobbler.UIAction.ListingSelected
import de.schnettler.scrobbler.UIError
import de.schnettler.scrobbler.components.ChipRow
import de.schnettler.scrobbler.components.ListTitle
import de.schnettler.scrobbler.components.LoadingScreen
import de.schnettler.scrobbler.components.SwipeRefreshProgressIndicator
import de.schnettler.scrobbler.components.SwipeToRefreshLayout
import de.schnettler.scrobbler.screens.details.AlbumDetailScreen
import de.schnettler.scrobbler.screens.details.ArtistDetailScreen
import de.schnettler.scrobbler.screens.details.TrackDetailScreen
import de.schnettler.scrobbler.theme.AppColor
import de.schnettler.scrobbler.viewmodels.DetailViewModel
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun DetailScreen(
    model: DetailViewModel,
    actionHandler: (UIAction) -> Unit,
    errorHandler: @Composable (UIError) -> Unit,
    modifier: Modifier = Modifier
) {
    val detailState by model.state.collectAsState()
    if (detailState.isError) {
        errorHandler(
            UIError.ShowErrorSnackbar(
                state = detailState,
                fallbackMessage = stringResource(id = R.string.error_details),
                onAction = model::refresh
            )
        )
    }

    if (detailState.isLoading) { LoadingScreen() } else {
        SwipeToRefreshLayout(
            refreshingState = detailState.isRefreshing,
            onRefresh = { model.refresh() },
            refreshIndicator = { SwipeRefreshProgressIndicator() }
        ) {
            detailState.currentData.let { details ->
                when (details) {
                    is ArtistWithStatsAndInfo -> ArtistDetailScreen(
                        artistInfo = details,
                        actionHandler = actionHandler,
                    )
                    is TrackWithStatsAndInfo -> TrackDetailScreen(
                        details,
                        actionHandler,
                        modifier,
                    )
                    is AlbumDetails -> AlbumDetailScreen(
                        albumDetails = details,
                        actionHandler = actionHandler,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayout::class)
@Composable
fun TagCategory(tags: List<String>, actionHandler: (UIAction) -> Unit) {
    ListTitle(title = stringResource(id = R.string.header_tags))
    ChipRow(items = tags, onChipClicked = { actionHandler(UIAction.TagSelected(it)) })
}

@Composable
fun AlbumCategory(
    album: LastFmEntity.Album?,
    artistPlaceholder: String,
    actionHandler: (UIAction) -> Unit
) {
    ListTitle(title = stringResource(id = R.string.track_sourcealbum))
    ListItem(
        text = {
            Text(album?.name ?: stringResource(id = R.string.track_unknownalbum))
        },
        secondaryText = {
            Text(album?.artist ?: artistPlaceholder)
        },
        icon = {
            Surface(
                color = AppColor.BackgroundElevated,
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(modifier = Modifier.preferredSize(60.dp)) {
                    album?.imageUrl?.let {
                        CoilImage(
                            data = it,
                            fadeIn = true,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        },
        modifier = Modifier.clickable(onClick = {
            actionHandler(ListingSelected(album ?: LastFmEntity.Artist(name = artistPlaceholder, url = "")))
        })
    )
}