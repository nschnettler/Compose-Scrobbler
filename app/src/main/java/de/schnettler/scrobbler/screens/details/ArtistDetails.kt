package de.schnettler.scrobbler.screens.details

import androidx.compose.Composable
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.foundation.Text
import androidx.ui.layout.Stack
import androidx.ui.layout.aspectRatio
import androidx.ui.layout.fillMaxSize
import androidx.ui.material.ListItem
import androidx.ui.material.Surface
import androidx.ui.unit.dp
import de.schnettler.database.models.EntityWithStats.TrackWithStats
import de.schnettler.database.models.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.components.ListeningStats
import de.schnettler.scrobbler.components.ListingScroller
import de.schnettler.scrobbler.components.PlainListIconBackground
import de.schnettler.scrobbler.components.TitleComponent
import de.schnettler.scrobbler.util.PlaysStyle
import de.schnettler.scrobbler.util.formatter
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade

@Composable
fun ArtistDetailScreen(
    artistInfo: ArtistWithStatsAndInfo,
    onListingSelected: (LastFmEntity) -> Unit,
    onTagClicked: (String) -> Unit
) {
    val (artist, stats, info) = artistInfo
    ScrollableColumn {
        Backdrop(imageUrl = artist.imageUrl, modifier = Modifier.aspectRatio(16 / 10f))
        AlbumDescription(description = info.wiki)
        ListeningStats(item = stats)
        TagCategory(tags = info.tags, onTagClicked = onTagClicked)
        TitleComponent(title = "Top Tracks")
        TrackListWithStats(tracks = artistInfo.topTracks, onListingSelected = onListingSelected)

        ListingScroller(
            title = "Top Albums",
            content = artistInfo.topAlbums,
            height = 160.dp,
            playsStyle = PlaysStyle.PUBLIC_PLAYS,
            onEntrySelected = onListingSelected
        )

        ListingScroller(
            title = "Ähnliche Künstler",
            content = artistInfo.similarArtists,
            height = 136.dp,
            playsStyle = PlaysStyle.NO_PLAYS,
            onEntrySelected = onListingSelected
        )
    }
}

@Composable
fun TrackListWithStats(tracks: List<TrackWithStats>, onListingSelected: (LastFmEntity) -> Unit) {
    tracks.forEachIndexed { index, (track, stats) ->
        ListItem(
            text = { Text(track.name) },
            secondaryText = {
                Text(formatter.format(stats.listeners).toString() + " Hörer")
            },
            icon = { PlainListIconBackground { Text(text = "${index + 1}") } },
            onClick = { onListingSelected.invoke(track) }
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