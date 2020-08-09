package de.schnettler.scrobbler.screens.preview

import androidx.ui.tooling.preview.PreviewParameterProvider
import de.schnettler.database.models.LocalTrack
import de.schnettler.database.models.ScrobbleStatus

class FakeHistoryTrackProvider : PreviewParameterProvider<LocalTrack> {
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