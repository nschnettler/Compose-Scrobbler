package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.layout.Column
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredWidth
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import de.schnettler.database.models.Artist
import de.schnettler.scrobbler.components.GenericHorizontalListingScroller
import de.schnettler.scrobbler.components.ListingCard
import de.schnettler.scrobbler.components.NewListingCard
import de.schnettler.scrobbler.util.ThemedPreview

@Preview
@Composable
fun ListingCardPreview() {
    ThemedPreview() {
        Column {
            val artist = Artist(
                name = "Dreamcatcherdgdbsdbdbdsbdsb",
                url = "Url",
                userPlays = 10,
                plays = 20)
            Box(modifier = Modifier.preferredHeight(150.dp)) {
                NewListingCard(
                    onEntrySelected = {},
                    data = artist,
                    plays = 10)
            }

            ListingCard(
                onEntrySelected = {},
                data = artist)
        }

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
            items = artists,
            height = 200.dp
        ) { listing ->
            ListingCard(data = listing, onEntrySelected = {})
        }
    }
}