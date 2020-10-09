package de.schnettler.scrobbler.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import de.schnettler.database.models.LastFmEntity.Artist
import de.schnettler.scrobbler.components.MediaCard
import de.schnettler.scrobbler.components.Recyclerview
import de.schnettler.scrobbler.util.Orientation

@Preview
@Composable
fun ListingCardPreview() {
        Column {
            val artist = Artist(
                name = "Dreamcatcher",
                url = "Url"
            )
            MediaCard(
                name = artist.name,
                plays = 10,
                imageUrl = null
            ) {}
            MediaCard(
                name = artist.name,
                plays = -1,
                imageUrl = null
            ) {}
        }
}

@Preview
@Composable
fun HorizontalListingScrollerPreview() {
    val artists = listOf(
        Artist(
            name = "Dreamcatcher",
            url = "Url"
        ),
        Artist(
            name = "All time Low",
            url = "Url")
    )
    Recyclerview(
        items = artists,
        height = 200.dp,
        orientation = Orientation.Horizontal
    ) { listing ->
        MediaCard(name = listing.name) {}
    }
}