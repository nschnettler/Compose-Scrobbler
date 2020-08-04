package de.schnettler.repo.mapping

import de.schnettler.database.models.EntityWithStats.AlbumWithStats
import de.schnettler.database.models.LastFmEntity.Album
import de.schnettler.database.models.Stats
import de.schnettler.lastfm.models.AlbumDto
import javax.inject.Inject

class AlbumWithStatsMapper @Inject constructor() : Mapper<AlbumDto, AlbumWithStats> {
    override suspend fun map(from: AlbumDto): AlbumWithStats {
        val album = Album(
            name = from.name,
            url = from.url,
            artist = from.artist.name,
            imageUrl = from.images.lastOrNull()?.url
        )
        return AlbumWithStats(
            entity = album,
            stats = Stats(id = album.id, plays = from.playcount)
        )
    }
}