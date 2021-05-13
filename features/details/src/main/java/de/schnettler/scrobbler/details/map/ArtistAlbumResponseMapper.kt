package de.schnettler.scrobbler.details.map

import de.schnettler.scrobbler.core.map.AlbumMapper
import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.core.map.StatsMapper
import de.schnettler.scrobbler.details.model.ArtistAlbumResponse
import de.schnettler.scrobbler.model.EntityWithStats.AlbumWithStats

object ArtistAlbumResponseMapper : Mapper<ArtistAlbumResponse, AlbumWithStats> {
    override suspend fun map(from: ArtistAlbumResponse): AlbumWithStats {
        val album = AlbumMapper.map(from)
        val stats = StatsMapper.map(from, album.id)
        return AlbumWithStats(entity = album, stats = stats)
    }
}