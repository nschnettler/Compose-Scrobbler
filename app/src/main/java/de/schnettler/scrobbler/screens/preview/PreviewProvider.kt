package de.schnettler.scrobbler.screens.preview

import androidx.ui.tooling.preview.PreviewParameterProvider
import de.schnettler.database.models.Artist
import de.schnettler.database.models.LocalTrack
import de.schnettler.database.models.ScrobbleStatus
import de.schnettler.database.models.Track
import de.schnettler.scrobbler.screens.mapToLastFmTrack

class FakeHistoryTrackProvider: PreviewParameterProvider<Track> {
    override val values = sequenceOf(
            generateTrack(ScrobbleStatus.PLAYING),
            generateTrack(ScrobbleStatus.LOCAL))

    private fun generateTrack(status: ScrobbleStatus): Track {
        return LocalTrack(
                title = "Red Sun",
                artist = "Dreamcatcher",
                album = "Dystopia",
                duration = 200L,
                status = status,
                playedBy = ""
        ).mapToLastFmTrack()
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