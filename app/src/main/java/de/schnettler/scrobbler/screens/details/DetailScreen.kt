package de.schnettler.scrobbler.screens.details

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.getValue
import androidx.ui.core.ContentScale
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.material.ListItem
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.res.colorResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import de.schnettler.database.models.*
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.LiveDataLoadingComponent
import de.schnettler.scrobbler.components.SwipeRefreshPrograssIndicator
import de.schnettler.scrobbler.components.SwipeToRefreshLayout
import de.schnettler.scrobbler.components.TitleComponent
import de.schnettler.scrobbler.util.currentData
import de.schnettler.scrobbler.util.loading
import de.schnettler.scrobbler.util.refreshing
import de.schnettler.scrobbler.viewmodels.DetailViewModel
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade

@Composable
fun DetailScreen(model: DetailViewModel, onListingSelected: (CommonEntity) -> Unit, onTagClicked: (String) -> Unit) {
    val artistState by model.state.collectAsState()

     if (artistState.loading) {
         LiveDataLoadingComponent()
      } else {
         SwipeToRefreshLayout(
             refreshingState = artistState.refreshing,
             onRefresh = { model.refresh() },
             refreshIndicator = { SwipeRefreshPrograssIndicator() }
         ) {
             artistState.currentData?.let {details ->
                 when(details) {
                     is Artist -> ArtistDetailScreen(artist = details, onListingSelected = onListingSelected, onTagClicked = onTagClicked)
                     is TrackDomain -> TrackDetailScreen(details, onTagClicked = onTagClicked)
                     is Album -> AlbumDetailScreen(album = details, onListingSelected = onListingSelected, onTagClicked = onTagClicked)
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
fun AlbumCategory(album: Album) {
    TitleComponent(title = "Aus dem Album")
    ListItem(
        text = {
            Text(album.name)
        },
        secondaryText = {
            Text("${album.artist}")
        },
        icon = {
            Surface(color = colorResource(id = R.color.colorStroke), shape = RoundedCornerShape(8.dp)) {
                Box(modifier = Modifier.preferredHeight(60.dp) + Modifier.preferredWidth(60.dp)) {
                    album.imageUrl?.let {
                        CoilImageWithCrossfade(
                            data = it,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

        })
}

@ExperimentalLayout
@Composable
fun ChipRow(items: List<String>, onChipClicked: (String) -> Unit = {}) {
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 16.dp) {
            items.forEach {
                Box(modifier = Modifier.clickable(onClick = { onChipClicked.invoke(it) })) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.body2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.drawBackground(
                            color = colorResource(id = R.color.colorBackgroundElevated),
                            shape = RoundedCornerShape(25.dp)
                        ) + Modifier.padding(horizontal = 12.dp, vertical = 8.dp) + Modifier.fillMaxHeight()
                    )
                }
            }
        }
    }
}