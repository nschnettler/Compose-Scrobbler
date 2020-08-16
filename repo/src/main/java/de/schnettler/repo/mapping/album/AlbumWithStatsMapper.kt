package de.schnettler.repo.mapping.album

import de.schnettler.database.models.EntityWithStats.AlbumWithStats
import de.schnettler.lastfm.models.AlbumDto
import de.schnettler.repo.mapping.AlbumMapper
import de.schnettler.repo.mapping.StatMapper
import de.schnettler.repo.mapping.Mapper
import javax.inject.Inject

class AlbumWithStatsMapper @Inject constructor() : Mapper<AlbumDto, AlbumWithStats> {
    override suspend fun map(from: AlbumDto): AlbumWithStats {
        val album = AlbumMapper.map(from)
        val stats = StatMapper.map(from, album.id)
        return AlbumWithStats(entity = album, stats = stats)
    }
}