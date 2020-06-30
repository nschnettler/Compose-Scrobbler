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
import de.schnettler.scrobbler.components.TitleComponent
import de.schnettler.scrobbler.viewmodels.DetailViewModel
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade

@Composable
fun DetailScreen(model: DetailViewModel, onListingSelected: (ListingMin) -> Unit) {
    val artistState by model.entryState.collectAsState()

    artistState.handleIfError(ContextAmbient.current)

    artistState.data?.let {details ->
        when(details) {
            is Artist -> ArtistDetailScreen(artist = details, onListingSelected = onListingSelected)
            is TrackDomain -> TrackDetailScreen(details)
        }

    }
}

@Composable
fun TagCategory(tags: List<String>) {
    TitleComponent(title = "Tags")
    ChipRow(items = tags)
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
                        CoilImageWithCrossfade(data = it, contentScale = ContentScale.Crop)
                    }
                }
            }

        })
}

@Composable
fun ChipRow(items: List<String>) {
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 16.dp) {
            items.forEach {
                Box(modifier = Modifier.clickable(onClick = {})) {
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