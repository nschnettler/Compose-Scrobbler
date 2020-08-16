package de.schnettler.repo.mapping.album

import de.schnettler.database.models.EntityWithStats.AlbumWithStats
import de.schnettler.lastfm.models.AlbumDto
import de.schnettler.repo.mapping.BaseAlbumMapper
import de.schnettler.repo.mapping.BaseStatMapper
import de.schnettler.repo.mapping.Mapper
import javax.inject.Inject

class AlbumWithStatsMapper @Inject constructor() : Mapper<AlbumDto, AlbumWithStats> {
    override suspend fun map(from: AlbumDto): AlbumWithStats {
        val album = BaseAlbumMapper.map(from)
        val stats = BaseStatMapper.map(from).copy(id = album.id)
        return AlbumWithStats(entity = album, stats = stats)
    }
}