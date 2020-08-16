package de.schnettler.repo.mapping.artist

import de.schnettler.database.models.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.lastfm.models.ArtistInfoDto
import de.schnettler.repo.mapping.ArtistMapper
import de.schnettler.repo.mapping.InfoMapper
import de.schnettler.repo.mapping.Mapper
import de.schnettler.repo.mapping.StatMapper

object ArtistInfoMapper : Mapper<ArtistInfoDto, ArtistWithStatsAndInfo> {
    override suspend fun map(from: ArtistInfoDto): ArtistWithStatsAndInfo {
        val artist = ArtistMapper.map(from)
        val stats = StatMapper.map(from.stats, artist.id)
        val info = InfoMapper.map(from, artist.id)
        return ArtistWithStatsAndInfo(artist, stats, info)
    }
}