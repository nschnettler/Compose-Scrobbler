package de.schnettler.scrobbler.screens.details

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.getValue
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.material.ListItem
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.material.ripple.ripple
import androidx.ui.res.colorResource
import androidx.ui.res.vectorResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import de.schnettler.database.models.*
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.TitleComponent
import de.schnettler.scrobbler.screens.formatter
import de.schnettler.scrobbler.util.defaultSpacerSize
import de.schnettler.scrobbler.viewmodels.DetailViewModel
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade
import timber.log.Timber

@Composable
fun DetailScreen(model: DetailViewModel) {
    val artistState by model.entryState.collectAsState(initial = null)

    Timber.d("Error ${artistState?.error}")
    artistState?.data?.let {details ->
        when(details) {
            is Artist -> ArtistDetailScreen(artist = details)
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
fun StatsRow(item: ListingMin) {
    Row(modifier = Modifier.fillMaxWidth() + Modifier.padding(vertical = defaultSpacerSize), horizontalArrangement = Arrangement.Center) {
        Column(horizontalGravity = Alignment.CenterHorizontally) {
            Icon(asset = vectorResource(id = R.drawable.ic_round_play_circle_outline_24))
            Text(text = formatter.format(item.plays))
        }
        Spacer(modifier = Modifier.width(64.dp))
        Column(horizontalGravity = Alignment.CenterHorizontally) {
            Icon(asset = vectorResource(id = R.drawable.ic_round_hearing_24))
            Text(text = formatter.format(item.userPlays))
        }
        Spacer(modifier = Modifier.width(64.dp))
        Column(horizontalGravity = Alignment.CenterHorizontally) {
            Icon(asset = vectorResource(id = R.drawable.ic_outline_account_circle_32))
            Text(text = formatter.format(item.listeners))
        }
    }
}



@Composable
fun ChipRow(items: List<String>) {
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 16.dp) {
            items.forEach {
                Clickable(onClick = {}, modifier = Modifier.ripple()) {
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