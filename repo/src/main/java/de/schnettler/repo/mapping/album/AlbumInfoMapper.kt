package de.schnettler.repo.mapping.album

import de.schnettler.database.models.EntityWithInfo
import de.schnettler.database.models.EntityWithStatsAndInfo.AlbumWithStatsAndInfo
import de.schnettler.lastfm.models.AlbumInfoDto
import de.schnettler.repo.mapping.AlbumMapper
import de.schnettler.repo.mapping.InfoMapper
import de.schnettler.repo.mapping.Mapper
import de.schnettler.repo.mapping.StatMapper
import de.schnettler.repo.mapping.TrackMapper

object AlbumInfoMapper : Mapper<AlbumInfoDto, AlbumWithStatsAndInfo> {
    override suspend fun map(from: AlbumInfoDto): AlbumWithStatsAndInfo {
        val album = AlbumMapper.map(from)
        val info = InfoMapper.map(from, album.id)
        val stats = StatMapper.map(from, album.id)
        val result = AlbumWithStatsAndInfo(entity = album, stats = stats, info = info)
        val tracks = from.tracks.track.map {
            val track = TrackMapper.map(it, album)
            EntityWithInfo.TrackWithInfo(track, InfoMapper.map(it, track.id))
        }
        result.tracks = tracks
        return result
    }
}