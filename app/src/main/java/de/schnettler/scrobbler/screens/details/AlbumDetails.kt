package de.schnettler.scrobbler.screens.details

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Border
import androidx.ui.foundation.Box
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.foundation.Text
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
import androidx.ui.material.MaterialTheme
import androidx.ui.res.colorResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import de.schnettler.database.models.Album
import de.schnettler.database.models.CommonEntity
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.ChipRow
import de.schnettler.scrobbler.components.ExpandingSummary
import de.schnettler.scrobbler.components.ListeningStats
import de.schnettler.scrobbler.util.cardCornerRadius
import de.schnettler.scrobbler.util.fromHtmlLastFm
import dev.chrisbanes.accompanist.coil.CoilImage

@OptIn(ExperimentalLayout::class)
@Composable
fun AlbumDetailScreen(
    album: Album,
    onListingSelected: (CommonEntity) -> Unit,
    onTagClicked: (String) -> Unit
) {
    ScrollableColumn {
        Column {
            Row(modifier = Modifier.padding(16.dp)) {
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
                        album.imageUrl?.let { url ->
                            CoilImage(data = url, modifier = Modifier.fillMaxSize())
                        }
                    }
                }
                Spacer(modifier = Modifier.preferredWidth(16.dp))
                Column(Modifier.padding(top = 8.dp)) {
                    Text(
                        text = album.name,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.h5
                    )
                    Text(
                        text = "von ${album.artist}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.subtitle1
                    )
                    Text(
                        text = "${album.tracks.size} Songs ⦁ ${album.getLength()} Minuten",
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