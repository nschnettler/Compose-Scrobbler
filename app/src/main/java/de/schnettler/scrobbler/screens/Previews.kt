package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.ui.tooling.preview.Preview
import de.schnettler.database.models.Artist
import de.schnettler.scrobbler.components.GenericHorizontalListingScroller
import de.schnettler.scrobbler.components.ListingCard
import de.schnettler.scrobbler.components.PlaysStyle
import de.schnettler.scrobbler.util.ThemedPreview

@Preview
@Composable
fun ListingCardPreview() {
    ThemedPreview() {
        ListingCard(
            onEntrySelected = {},
            data = Artist(
                name = "Dreamcatcher",
                url = "Url",
                userPlays = 10,
                plays = 20),
            playsStyle = PlaysStyle.USER_PLAYS)
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
    ThemedPreview() {
        GenericHorizontalListingScroller(
            items = artists
        ) { listing ->
            ListingCard(data = listing, onEntrySelected = {})
        }
    }
}