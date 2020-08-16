package de.schnettler.repo.mapping.album

import de.schnettler.database.models.EntityWithInfo
import de.schnettler.database.models.EntityWithStatsAndInfo.AlbumWithStatsAndInfo
import de.schnettler.lastfm.models.AlbumInfoDto
import de.schnettler.repo.mapping.BaseAlbumMapper
import de.schnettler.repo.mapping.BaseInfoMapper
import de.schnettler.repo.mapping.BaseStatMapper
import de.schnettler.repo.mapping.BaseTrackMapper
import de.schnettler.repo.mapping.Mapper
import javax.inject.Inject

class AlbumInfoMapper @Inject constructor() : Mapper<AlbumInfoDto, AlbumWithStatsAndInfo> {
    override suspend fun map(from: AlbumInfoDto): AlbumWithStatsAndInfo {
        val album = BaseAlbumMapper.map(from)
        val info = BaseInfoMapper.map(from, album.id)
        val stats = BaseStatMapper.map(from, album.id)
        val result = AlbumWithStatsAndInfo(entity = album, stats = stats, info = info)
        val tracks = from.tracks.track.map {
            val track = BaseTrackMapper.map(it, album)
            EntityWithInfo.TrackWithInfo(track, BaseInfoMapper.map(it, track.id))
        }
        result.tracks = tracks
        return result
    }
}