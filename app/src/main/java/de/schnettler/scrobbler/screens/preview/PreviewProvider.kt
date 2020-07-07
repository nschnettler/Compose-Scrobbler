package de.schnettler.scrobbler.screens.preview

import androidx.ui.tooling.preview.PreviewParameterProvider
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