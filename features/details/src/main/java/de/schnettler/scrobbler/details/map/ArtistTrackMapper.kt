package de.schnettler.scrobbler.details.map

import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.core.map.StatsMapper
import de.schnettler.scrobbler.core.map.TrackMapper
import de.schnettler.scrobbler.details.model.ArtistTrackResponse
import de.schnettler.scrobbler.model.EntityWithStats.TrackWithStats

object ArtistTrackMapper : Mapper<ArtistTrackResponse, TrackWithStats> {
    override suspend fun map(from: ArtistTrackResponse): TrackWithStats {
        val track = TrackMapper.map(from, null)
        val stats = StatsMapper.map(from, track.id)
        return TrackWithStats(entity = track, stats = stats)
    }
}