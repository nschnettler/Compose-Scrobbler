package de.schnettler.scrobbler.screens.details

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Border
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.material.Card
import androidx.ui.material.MaterialTheme
import androidx.ui.res.colorResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import de.schnettler.database.models.Album
import de.schnettler.database.models.ListingMin
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.ExpandingSummary
import de.schnettler.scrobbler.components.ListeningStats
import de.schnettler.scrobbler.util.cardCornerRadius
import de.schnettler.scrobbler.util.fromHtmlLastFm
import de.schnettler.scrobbler.util.sumByLong
import dev.chrisbanes.accompanist.coil.CoilImage
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalLayout::class)
@Composable
fun AlbumDetailScreen(album: Album, onListingSelected: (ListingMin) -> Unit, onTagClicked: (String) -> Unit) {
    VerticalScroller {
        Column {
            Row(modifier = Modifier.padding(16.dp)) {
                Card(modifier = Modifier.preferredWidth(182.dp)
                    .aspectRatio(1F)
                    .drawBackground(colorResource(id = R.color.colorStroke)
                    ), shape = RoundedCornerShape(cardCornerRadius)
                ) {
                    album.imageUrl?.let {url ->
                        CoilImage(data = url, modifier = Modifier.fillMaxSize())
                    }
                }
                Spacer(modifier = Modifier.preferredWidth(16.dp))
                Column(Modifier.padding(top = 8.dp)) {
                    Text(text = album.name,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.h5
                    )
                    Text(text = "von ${album.artist}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.subtitle1
                    )
                    Text(text = "${album.tracks.size} Songs ⦁ ${TimeUnit.SECONDS.toMinutes(album.tracks.sumByLong { it.duration })} Minuten",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.subtitle2
                    )
                }

            }
            ChipRow(items = album.tags, onChipClicked = onTagClicked)

            Spacer(modifier = Modifier.preferredHeight(16.dp))

            ListeningStats(item = album)

            album.description?.let {
                Card(
                    shape = RoundedCornerShape(cardCornerRadius),
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    elevation = 0.dp,
                    border = Border(1.dp, colorResource(id = R.color.colorStroke))
                ) {
                    ExpandingSummary(it.fromHtmlLastFm(), modifier = Modifier.padding(16.dp))
                }
            }

            Spacer(modifier = Modifier.preferredHeight(16.dp))

            TrackList(tracks = album.tracks, onListingSelected = onListingSelected)
        }
    }
}