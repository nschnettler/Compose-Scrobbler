package de.schnettler.scrobbler.screens.details

import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.drawBackground
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.EntityWithStats.TrackWithStats
import de.schnettler.database.models.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.components.ListeningStats
import de.schnettler.scrobbler.components.ListingScroller
import de.schnettler.scrobbler.components.LiveDataLoadingComponent
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
        Backdrop(
            imageUrl = artist.imageUrl,
            modifier = Modifier.aspectRatio(16 / 10f),
            placeholder = artist.name
        )
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
    placeholder: String,
    modifier: Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
            .drawBackground(MaterialTheme.colors.onSurface.copy(0.05F)),
        gravity = ContentGravity.Center
    ) {
        imageUrl?.let {
            CoilImageWithCrossfade(
                data = it,
                contentScale = ContentScale.Crop,
                loading = {
                    LiveDataLoadingComponent()
                },
                modifier = Modifier.fillMaxSize()
            )
        } ?: Text(text = placeholder, style = MaterialTheme.typography.h3)
    }
}