package de.schnettler.scrobbler.screens.details

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Border
import androidx.ui.foundation.Box
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Column
import androidx.ui.layout.ExperimentalLayout
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.aspectRatio
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredWidth
import androidx.ui.material.Card
import androidx.ui.material.ListItem
import androidx.ui.material.MaterialTheme
import androidx.ui.res.colorResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import de.schnettler.database.models.EntityWithStatsAndInfo.AlbumWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.ChipRow
import de.schnettler.scrobbler.components.ExpandingSummary
import de.schnettler.scrobbler.components.ListeningStats
import de.schnettler.scrobbler.components.PlainListIconBackground
import de.schnettler.scrobbler.util.cardCornerRadius
import de.schnettler.scrobbler.util.fromHtmlLastFm
import dev.chrisbanes.accompanist.coil.CoilImage

@OptIn(ExperimentalLayout::class)
@Composable
fun AlbumDetailScreen(
    albumDetails: AlbumWithStatsAndInfo,
    onListingSelected: (LastFmEntity) -> Unit,
    onTagClicked: (String) -> Unit
) {
    val (album, stats, info) = albumDetails
    ScrollableColumn {
        Row(modifier = Modifier.padding(16.dp)) {
            AlbumArtwork(url = album.imageUrl)
            Spacer(modifier = Modifier.preferredWidth(16.dp))
            AlbumInfo(
                name = album.name,
                artist = album.artist,
                tracks = albumDetails.tracks.size,
                duration = albumDetails.getLength(),
                onArtistSelected = onListingSelected
            )
        }
        ChipRow(items = albumDetails.info.tags, onChipClicked = onTagClicked)
        Spacer(modifier = Modifier.preferredHeight(16.dp))
        ListeningStats(item = stats)
        AlbumDescription(info.wiki.fromHtmlLastFm())
        Spacer(modifier = Modifier.preferredHeight(16.dp))
        TrackList(
            tracks = albumDetails.tracks.map { it.entity },
            onListingSelected = onListingSelected
        )
    }
}

@Composable
fun AlbumArtwork(url: String?) {
    Card(
        modifier = Modifier.preferredWidth(182.dp)
            .aspectRatio(1F),
        shape = RoundedCornerShape(cardCornerRadius)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().drawBackground(
                colorResource(id = R.color.colorStroke)
            )
        ) {
            url?.let { url ->
                CoilImage(data = url, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun TrackList(tracks: List<LastFmEntity.Track>, onListingSelected: (LastFmEntity) -> Unit) {
    tracks.forEachIndexed { index, track ->
        ListItem(
            text = { Text(track.name) },
            icon = { PlainListIconBackground { Text(text = "${index + 1}") } },
            onClick = { onListingSelected.invoke(track) }
        )
    }
}

@Composable
fun AlbumInfo(
    name: String,
    artist: String,
    tracks: Int,
    duration: Long,
    onArtistSelected: (LastFmEntity.Artist) -> Unit
) {
    Column(Modifier.padding(top = 8.dp)) {
        Text(
            text = name,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.h5
        )
        Text(
            text = "von $artist",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.clickable(onClick = {
                onArtistSelected(LastFmEntity.Artist(name = artist, url = ""))
            })
        )
        Text(
            text = "$tracks Songs ⦁ $duration Minuten",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.subtitle2
        )
    }
}

@Composable
fun AlbumDescription(description: String?) {
    if (!description.isNullOrBlank()) {
        Card(
            shape = RoundedCornerShape(cardCornerRadius),
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            elevation = 0.dp,
            border = Border(1.dp, colorResource(id = R.color.colorStroke))
        ) {
            ExpandingSummary(description, modifier = Modifier.padding(16.dp))
        }
    }
}