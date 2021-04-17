package de.schnettler.repo.mapping.track

import de.schnettler.lastfm.models.TrackInfoDto
import de.schnettler.scrobbler.core.map.AlbumMapper
import de.schnettler.scrobbler.core.map.InfoMapper
import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.core.map.StatMapper
import de.schnettler.scrobbler.core.map.TrackMapper
import de.schnettler.scrobbler.model.EntityWithStatsAndInfo.TrackWithStatsAndInfo

object TrackInfoMapper : Mapper<TrackInfoDto, TrackWithStatsAndInfo> {
    override suspend fun map(from: TrackInfoDto): TrackWithStatsAndInfo {
        val album = from.album?.let { album -> AlbumMapper.map(album) }
        val track = TrackMapper.map(from, album)
        val stats = StatMapper.map(from, track.id)
        // Duration is in ms here.
        val info = InfoMapper.map(from.copy(duration = from.duration / 1000), track.id)
        return TrackWithStatsAndInfo(entity = track, stats = stats, info = info, album = album)
    }
}