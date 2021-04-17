package de.schnettler.repo.mapping.artist

import de.schnettler.scrobbler.core.model.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.lastfm.models.ArtistInfoDto
import de.schnettler.scrobbler.core.map.ArtistMapper
import de.schnettler.scrobbler.core.map.InfoMapper
import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.core.map.StatMapper
import de.schnettler.scrobbler.core.map.forLists

object ArtistInfoMapper : Mapper<ArtistInfoDto, ArtistWithStatsAndInfo> {
    override suspend fun map(from: ArtistInfoDto): ArtistWithStatsAndInfo {
        val artist = ArtistMapper.map(from)
        val stats = StatMapper.map(from.stats, artist.id)
        val info = InfoMapper.map(from, artist.id)
        val similar = ArtistMapper.forLists()(from.similar.artist)
        return ArtistWithStatsAndInfo(artist, stats, info).apply { similarArtists = similar }
    }
}