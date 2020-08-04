package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.ui.layout.Column
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import de.schnettler.database.models.LastFmEntity.Artist
import de.schnettler.scrobbler.components.Recyclerview
import de.schnettler.scrobbler.components.ListingCard
import de.schnettler.scrobbler.util.Orientation

@Preview
@Composable
fun ListingCardPreview() {
        Column {
            val artist = Artist(
                name = "Dreamcatcher",
                url = "Url"
            )
            ListingCard(
                onEntrySelected = {},
                plays = 10,
                name = artist.name,
                imageUrl = null
            )
            ListingCard(
                onEntrySelected = {},
                plays = -1,
                name = artist.name,
                imageUrl = null
            )
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
        ListingCard(name = listing.name) {}
    }
}