package de.schnettler.scrobbler.details.map

import de.schnettler.scrobbler.core.map.ArtistMapper
import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.core.map.StatsMapper
import de.schnettler.scrobbler.core.map.forLists
import de.schnettler.scrobbler.details.model.ArtistInfoResponse
import de.schnettler.scrobbler.model.EntityWithStatsAndInfo.ArtistWithStatsAndInfo

object ArtistInfoMapper : Mapper<ArtistInfoResponse, ArtistWithStatsAndInfo> {
    override suspend fun map(from: ArtistInfoResponse): ArtistWithStatsAndInfo {
        val artist = ArtistMapper.map(from)
        val stats = StatsMapper.map(from.stats, artist.id)
        val info = InfoMapper.map(from, artist.id)
        val similar = ArtistMapper.forLists()(from.similar.artist)
        return ArtistWithStatsAndInfo(artist, stats, info).apply { similarArtists = similar }
    }
}