package de.schnettler.repo.mapping

import de.schnettler.database.models.EntityInfo
import de.schnettler.database.models.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity.Artist
import de.schnettler.database.models.Stats
import de.schnettler.lastfm.models.ArtistInfoDto
import javax.inject.Inject

class ArtistInfoMapper @Inject constructor() : Mapper<ArtistInfoDto, ArtistWithStatsAndInfo> {
    override suspend fun map(from: ArtistInfoDto): ArtistWithStatsAndInfo {
        val artist = Artist(
            name = from.name,
            url = from.url
        )
        val stats = Stats(
            id = artist.id,
            plays = from.stats.playcount,
            listeners = from.stats.listeners,
            userPlays = from.stats.userplaycount ?: 0
        )
        val info = EntityInfo(
            id = artist.id,
            tags = from.tags.tag.map { tag -> tag.name },
            wiki = from.bio.content
        )
        return ArtistWithStatsAndInfo(artist, stats, info)
    }
}