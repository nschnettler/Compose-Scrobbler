package de.schnettler.scrobbler.details.map

import de.schnettler.scrobbler.core.map.AlbumMapper
import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.core.map.StatsMapper
import de.schnettler.scrobbler.core.map.TrackMapper
import de.schnettler.scrobbler.details.model.TrackInfoResponse
import de.schnettler.scrobbler.model.EntityWithStatsAndInfo.TrackWithStatsAndInfo

object TrackInfoMapper : Mapper<TrackInfoResponse, TrackWithStatsAndInfo> {
    override suspend fun map(from: TrackInfoResponse): TrackWithStatsAndInfo {
        val album = from.album?.let { album -> AlbumMapper.map(album) }
        val track = TrackMapper.map(from, album)
        val stats = StatsMapper.map(from, track.id)
        // Duration is in ms here.
        val info = InfoMapper.map(from.copy(duration = from.duration / 1000), track.id)
        return TrackWithStatsAndInfo(entity = track, stats = stats, info = info, album = album)
    }
}