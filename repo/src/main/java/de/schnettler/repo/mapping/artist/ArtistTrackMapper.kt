package de.schnettler.repo.mapping.artist

import de.schnettler.database.models.EntityWithStats.TrackWithStats
import de.schnettler.lastfm.models.ArtistTracksDto
import de.schnettler.repo.mapping.Mapper
import de.schnettler.repo.mapping.StatMapper
import de.schnettler.repo.mapping.TrackMapper

object ArtistTrackMapper : Mapper<ArtistTracksDto, TrackWithStats> {
    override suspend fun map(from: ArtistTracksDto): TrackWithStats {
        val track = TrackMapper.map(from, null)
        val stats = StatMapper.map(from, track.id)
        return TrackWithStats(entity = track, stats = stats)
    }
}