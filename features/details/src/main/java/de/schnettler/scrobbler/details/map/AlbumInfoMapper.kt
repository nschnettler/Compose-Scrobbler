package de.schnettler.scrobbler.details.map

import de.schnettler.scrobbler.core.map.AlbumMapper
import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.core.map.StatsMapper
import de.schnettler.scrobbler.core.map.TrackMapper
import de.schnettler.scrobbler.details.model.AlbumDetailEntity
import de.schnettler.scrobbler.details.model.AlbumInfoResponse
import de.schnettler.scrobbler.model.EntityWithInfo

object AlbumInfoMapper : Mapper<AlbumInfoResponse, AlbumDetailEntity> {
    override suspend fun map(from: AlbumInfoResponse): AlbumDetailEntity {
        val album = AlbumMapper.map(from)
        val info = InfoMapper.map(from, album.id)
        val stats = StatsMapper.map(from, album.id)
        val result = AlbumDetailEntity(album = album, stats = stats, info = info, artist = null)
        val tracks = from.tracks.track.map {
            val track = TrackMapper.map(it, album)
            EntityWithInfo.TrackWithInfo(track, InfoMapper.map(it, track.id))
        }
        result.tracks = tracks
        return result
    }
}