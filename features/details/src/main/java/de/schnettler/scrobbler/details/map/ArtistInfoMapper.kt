package de.schnettler.scrobbler.details.map

import de.schnettler.scrobbler.core.map.ArtistMapper
import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.core.map.StatsMapper
import de.schnettler.scrobbler.core.map.forLists
import de.schnettler.scrobbler.details.model.ArtistDetailEntity
import de.schnettler.scrobbler.details.model.ArtistInfoResponse

object ArtistInfoMapper : Mapper<ArtistInfoResponse, ArtistDetailEntity> {
    override suspend fun map(from: ArtistInfoResponse): ArtistDetailEntity {
        val artist = ArtistMapper.map(from)
        val stats = StatsMapper.map(from.stats, artist.id)
        val info = InfoMapper.map(from, artist.id)
        val similar = ArtistMapper.forLists()(from.similar.artist)
        return ArtistDetailEntity(artist, stats, info).apply { similarArtists = similar }
    }
}