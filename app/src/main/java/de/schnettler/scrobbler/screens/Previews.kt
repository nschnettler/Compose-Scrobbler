package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.ui.layout.Column
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import de.schnettler.database.models.Artist
import de.schnettler.scrobbler.components.Recyclerview
import de.schnettler.scrobbler.components.ListingCard
import de.schnettler.scrobbler.util.Orientation

@Preview
@Composable
fun ListingCardPreview() {
        Column {
            val artist = Artist(
                name = "Dreamcatcher",
                url = "Url",
                userPlays = 10,
                plays = 20)
            ListingCard(
                onEntrySelected = {},
                data = artist,
                plays = 10)
            ListingCard(
                onEntrySelected = {},
                data = artist,
                plays = -1)
        }
}

@Preview
@Composable
fun HorizontalListingScrollerPreview() {
    val artists = listOf(
        Artist(
            name = "Dreamcatcher",
            url = "Url",
            userPlays = 10,
            plays = 20
        ),
        Artist(
            name = "All time Low",
            url = "Url",
            userPlays = 10,
            plays = 20
        )
    )
    Recyclerview(
        items = artists,
        height = 200.dp,
        orientation = Orientation.Horizontal
    ) { listing ->
        ListingCard(data = listing, onEntrySelected = {})
    }
}