package de.schnettler.repo.mapping

import de.schnettler.database.models.EntityWithStats.TrackWithStats
import de.schnettler.database.models.LastFmEntity.Track
import de.schnettler.database.models.Stats
import de.schnettler.lastfm.models.ArtistTracksDto
import javax.inject.Inject

class ArtistTrackMapper @Inject constructor() : Mapper<ArtistTracksDto, TrackWithStats> {
    override suspend fun map(from: ArtistTracksDto): TrackWithStats {
        val track = Track(
            name = from.name,
            url = from.url,
            artist = from.artist.name
        )
        return TrackWithStats(
            entity = track,
            stats = Stats(id = track.id, plays = from.playcount, listeners = from.listeners)
        )
    }
}