package de.schnettler.repo.mapping.album

import de.schnettler.lastfm.models.AlbumDto
import de.schnettler.scrobbler.core.map.AlbumMapper
import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.core.map.StatMapper
import de.schnettler.scrobbler.model.EntityWithStats.AlbumWithStats

object AlbumWithStatsMapper : Mapper<AlbumDto, AlbumWithStats> {
    override suspend fun map(from: AlbumDto): AlbumWithStats {
        val album = AlbumMapper.map(from)
        val stats = StatMapper.map(from, album.id)
        return AlbumWithStats(entity = album, stats = stats)
    }
}