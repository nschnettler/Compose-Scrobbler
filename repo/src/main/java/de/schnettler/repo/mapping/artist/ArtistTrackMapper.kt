package de.schnettler.repo.mapping.artist

import de.schnettler.lastfm.models.ArtistTracksDto
import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.core.map.StatMapper
import de.schnettler.scrobbler.core.map.TrackMapper
import de.schnettler.scrobbler.core.model.EntityWithStats.TrackWithStats

object ArtistTrackMapper : Mapper<ArtistTracksDto, TrackWithStats> {
    override suspend fun map(from: ArtistTracksDto): TrackWithStats {
        val track = TrackMapper.map(from, null)
        val stats = StatMapper.map(from, track.id)
        return TrackWithStats(entity = track, stats = stats)
    }
}