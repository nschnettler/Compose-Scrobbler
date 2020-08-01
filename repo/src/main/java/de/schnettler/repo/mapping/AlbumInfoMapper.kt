package de.schnettler.repo.mapping

import de.schnettler.database.models.EntityInfo
import de.schnettler.database.models.EntityWithStatsAndInfo.AlbumWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity.Album
import de.schnettler.database.models.Stats
import de.schnettler.lastfm.models.AlbumInfoDto
import javax.inject.Inject

class AlbumInfoMapper @Inject constructor() : Mapper<AlbumInfoDto, AlbumWithStatsAndInfo> {
    override suspend fun map(from: AlbumInfoDto): AlbumWithStatsAndInfo {
        val album = Album(
            name = from.name,
            url = from.url,
            artist = from.artist
        )
        val info = EntityInfo(
            id = album.id,
            tags = from.tags.tag.map { tag -> tag.name },
            wiki = from.wiki?.summary ?: ""
        )
        val stats = Stats(
            id = album.id,
            plays = from.playcount,
            listeners = from.listeners,
            userPlays = from.userplaycount
        )
        return AlbumWithStatsAndInfo(entity = album, stats = stats, info = info)
    }
}