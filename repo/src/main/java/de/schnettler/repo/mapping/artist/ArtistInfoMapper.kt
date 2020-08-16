package de.schnettler.repo.mapping.artist

import de.schnettler.database.models.EntityInfo
import de.schnettler.database.models.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.lastfm.models.ArtistInfoDto
import de.schnettler.repo.mapping.BaseArtistMapper
import de.schnettler.repo.mapping.BaseStatMapper
import de.schnettler.repo.mapping.Mapper
import javax.inject.Inject

class ArtistInfoMapper @Inject constructor() : Mapper<ArtistInfoDto, ArtistWithStatsAndInfo> {
    override suspend fun map(from: ArtistInfoDto): ArtistWithStatsAndInfo {
        val artist = BaseArtistMapper.map(from)
        val stats = BaseStatMapper.map(from.stats).copy(id = artist.id)
        val info = EntityInfo(
            id = artist.id,
            tags = from.tags.tag.map { tag -> tag.name },
            wiki = from.bio.content
        )
        return ArtistWithStatsAndInfo(artist, stats, info)
    }
}