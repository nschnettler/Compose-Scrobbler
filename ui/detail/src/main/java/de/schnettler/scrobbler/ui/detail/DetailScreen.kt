package de.schnettler.scrobbler.ui.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.CoilImage
import de.schnettler.scrobbler.compose.navigation.UIAction
import de.schnettler.scrobbler.compose.navigation.UIAction.ListingSelected
import de.schnettler.scrobbler.compose.navigation.UIError
import de.schnettler.scrobbler.compose.theme.AppColor
import de.schnettler.scrobbler.compose.widget.Header
import de.schnettler.scrobbler.core.model.EntityWithStatsAndInfo
import de.schnettler.scrobbler.core.model.LastFmEntity
import de.schnettler.scrobbler.core.ui.viewmodel.RefreshableStateViewModel2
import de.schnettler.scrobbler.ui.detail.screen.AlbumDetailScreen
import de.schnettler.scrobbler.ui.detail.screen.ArtistDetailScreen
import de.schnettler.scrobbler.ui.detail.screen.TrackDetailScreen
import de.schnettler.scrobbler.ui.detail.viewmodel.TrackViewModel

@Composable
fun <Key : Any, StateType : Any, Output : StateType> DetailScreen(
    viewModel: RefreshableStateViewModel2<Key, StateType, Output>,
    actioner: (UIAction) -> Unit,
    errorer: @Composable (UIError) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    RefreshableScreen(
        state = state,
        refresh = viewModel::refresh,
        errorer = errorer,
        errorId = R.string.error_details
    ) { details ->
        when (details) {
            is EntityWithStatsAndInfo.ArtistWithStatsAndInfo -> ArtistDetailScreen(info = details, actioner = actioner)
            is EntityWithStatsAndInfo.AlbumDetails -> AlbumDetailScreen(details = details, actioner = actioner)
            is EntityWithStatsAndInfo.TrackWithStatsAndInfo ->
                TrackDetailScreen(viewModel = viewModel as TrackViewModel, details = details, actioner = actioner)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlbumCategory(
    album: LastFmEntity.Album?,
    artistPlaceholder: String,
    actionHandler: (UIAction) -> Unit
) {
    Header(title = stringResource(id = R.string.track_sourcealbum))
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
                Box(modifier = Modifier.size(60.dp)) {
                    album?.imageUrl?.let {
                        CoilImage(
                            data = it,
                            fadeIn = true,
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
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