package de.schnettler.scrobbler.screens.details

import androidx.compose.Composable
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.Border
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Stack
import androidx.ui.layout.aspectRatio
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredWidth
import androidx.ui.material.Card
import androidx.ui.material.ListItem
import androidx.ui.material.Surface
import androidx.ui.res.colorResource
import androidx.ui.unit.dp
import de.schnettler.database.models.Artist
import de.schnettler.database.models.CommonEntity
import de.schnettler.database.models.Track
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.ExpandingSummary
import de.schnettler.scrobbler.components.ListeningStats
import de.schnettler.scrobbler.components.ListingScroller
import de.schnettler.scrobbler.components.TitleComponent
import de.schnettler.scrobbler.util.PlaysStyle
import de.schnettler.scrobbler.util.cardCornerRadius
import de.schnettler.scrobbler.util.defaultSpacerSize
import de.schnettler.scrobbler.util.formatter
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade

@Composable
fun ArtistDetailScreen(
    artist: Artist,
    onListingSelected: (CommonEntity) -> Unit,
    onTagClicked: (String) -> Unit
) {
    ScrollableColumn {
        Backdrop(imageUrl = artist.imageUrl, modifier = Modifier.aspectRatio(16 / 10f))

        Card(
            border = Border(1.dp, colorResource(id = R.color.colorStroke)),
            modifier = Modifier.padding(
                defaultSpacerSize
            ) + Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(
                cardCornerRadius
            )
        ) {
            ExpandingSummary(artist.bio, modifier = Modifier.padding(defaultSpacerSize))
        }

        ListeningStats(item = artist)

        TagCategory(tags = artist.tags, onTagClicked = onTagClicked)

        TitleComponent(title = "Top Tracks")
        TrackList(tracks = artist.topTracks, onListingSelected = onListingSelected)

        ListingScroller(
            title = "Top Albums",
            content = artist.topAlbums.sortedByDescending { it.plays },
            height = 160.dp,
            playsStyle = PlaysStyle.PUBLIC_PLAYS,
            onEntrySelected = onListingSelected
        )

        ListingScroller(
            title = "Ähnliche Künstler",
            content = artist.similarArtists,
            height = 136.dp,
            playsStyle = PlaysStyle.NO_PLAYS,
            onEntrySelected = onListingSelected
        )
    }
}

@Composable
fun TrackList(tracks: List<Track>, onListingSelected: (CommonEntity) -> Unit) {
    tracks.forEachIndexed { index, track ->
        ListItem(
            text = { Text(track.name) },
            secondaryText = {
                Text(formatter.format(track.listeners).toString() + " Hörer")
            },
            icon = {
                Surface(
                    color = colorResource(id = R.color.colorBackgroundElevated),
                    shape = CircleShape,
                    modifier = Modifier.preferredHeight(40.dp) + Modifier.preferredWidth(40.dp)
                ) {
                    Box(gravity = ContentGravity.Center) {
                        Text(text = "${index + 1}")
                    }
                }
            }, onClick = { onListingSelected.invoke(track) }
        )
    }
}

@Composable
private fun Backdrop(
    imageUrl: String?,
    modifier: Modifier
) {
    Surface(modifier = modifier) {
        Stack(Modifier.fillMaxSize()) {
            imageUrl?.let {
                CoilImageWithCrossfade(
                    data = it,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}