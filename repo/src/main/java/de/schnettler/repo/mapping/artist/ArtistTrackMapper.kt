package de.schnettler.repo.mapping.artist

import de.schnettler.database.models.EntityWithStats.TrackWithStats
import de.schnettler.lastfm.models.ArtistTracksDto
import de.schnettler.repo.mapping.BaseStatMapper
import de.schnettler.repo.mapping.BaseTrackMapper
import de.schnettler.repo.mapping.Mapper
import javax.inject.Inject

class ArtistTrackMapper @Inject constructor() : Mapper<ArtistTracksDto, TrackWithStats> {
    override suspend fun map(from: ArtistTracksDto): TrackWithStats {
        val track = BaseTrackMapper.map(from, null)
        val stats = BaseStatMapper.map(from, track.id)
        return TrackWithStats(entity = track, stats = stats)
    }
}