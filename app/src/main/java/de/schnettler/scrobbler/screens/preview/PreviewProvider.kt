package de.schnettler.scrobbler.screens.preview

import androidx.ui.tooling.preview.PreviewParameterProvider
import de.schnettler.database.models.*

class FakeHistoryTrackProvider: PreviewParameterProvider<LocalTrack> {
    override val values = sequenceOf(
            generateTrack(ScrobbleStatus.PLAYING),
            generateTrack(ScrobbleStatus.LOCAL))

    private fun generateTrack(status: ScrobbleStatus): LocalTrack {
        return LocalTrack(
                name = "Red Sun",
                artist = "Dreamcatcher",
                album = "Dystopia",
                duration = 200L,
                status = status,
                playedBy = ""
        )
    }
}

class FakeTopListEntry: PreviewParameterProvider<Artist> {
    override val values = sequenceOf(
        generateArtist())

    private fun generateArtist(): Artist {
        return Artist(name = "Dreamcatcher",
            url = "Url",
            userPlays = 10,
            plays = 20)
    }
}