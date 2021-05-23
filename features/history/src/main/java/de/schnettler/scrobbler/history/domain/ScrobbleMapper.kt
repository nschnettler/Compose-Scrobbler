package de.schnettler.scrobbler.history.domain

import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.history.model.RecentTrackResponse
import de.schnettler.scrobbler.model.Scrobble
import de.schnettler.scrobbler.model.ScrobbleStatus

object ScrobbleMapper : Mapper<RecentTrackResponse, Scrobble> {
    override suspend fun map(from: RecentTrackResponse) = Scrobble(
        name = from.name,
        artist = from.artist.name,
        album = from.album.name,
        duration = 1,
        timestamp = from.date?.uts ?: Long.MAX_VALUE,
        playedBy = "external",
        status = if (from.date != null) ScrobbleStatus.EXTERNAL else ScrobbleStatus.PLAYING
    )
}