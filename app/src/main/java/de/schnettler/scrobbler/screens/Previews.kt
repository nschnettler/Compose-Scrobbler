package de.schnettler.scrobbler.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.ui.tooling.preview.Preview
import de.schnettler.database.models.LastFmEntity.Artist
import de.schnettler.scrobbler.components.MediaCard

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