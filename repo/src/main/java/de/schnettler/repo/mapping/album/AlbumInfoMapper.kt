package de.schnettler.repo.mapping.album

import de.schnettler.database.models.EntityInfo
import de.schnettler.database.models.EntityWithInfo
import de.schnettler.database.models.EntityWithStatsAndInfo.AlbumWithStatsAndInfo
import de.schnettler.lastfm.models.AlbumInfoDto
import de.schnettler.repo.mapping.BaseAlbumMapper
import de.schnettler.repo.mapping.BaseStatMapper
import de.schnettler.repo.mapping.BaseTrackMapper
import de.schnettler.repo.mapping.Mapper
import javax.inject.Inject

class AlbumInfoMapper @Inject constructor() : Mapper<AlbumInfoDto, AlbumWithStatsAndInfo> {
    override suspend fun map(from: AlbumInfoDto): AlbumWithStatsAndInfo {
        val album = BaseAlbumMapper.map(from)
        val info = EntityInfo(
            id = album.id,
            tags = from.tags.tag.map { tag -> tag.name },
            wiki = from.wiki?.summary ?: ""
        )
        val stats = BaseStatMapper.map(from).copy(id = album.id)
        val result = AlbumWithStatsAndInfo(entity = album, stats = stats, info = info)
        val tracks = from.tracks.track.map {
            val track = BaseTrackMapper.map(it).copy(
                album = album.name,
                imageUrl = album.imageUrl
            )
            val info = EntityInfo(
                id = track.id,
                duration = it.duration,
                wiki = ""
            )
            EntityWithInfo.TrackWithInfo(track, info)
        }
        result.tracks = tracks
        return result
    }
}