package de.schnettler.repo.mapping.artist

import de.schnettler.database.models.EntityType
import de.schnettler.database.models.ListType
import de.schnettler.database.models.TopListArtist
import de.schnettler.lastfm.models.ChartArtistDto
import de.schnettler.repo.mapping.ArtistMapper
import de.schnettler.repo.mapping.IndexedMapper
import de.schnettler.repo.mapping.createTopListEntry
import javax.inject.Inject

class ChartArtistMapper @Inject constructor() : IndexedMapper<ChartArtistDto, TopListArtist> {
    override suspend fun map(index: Int, from: ChartArtistDto): TopListArtist {
        val artist = ArtistMapper.map(from)
        val toplist = createTopListEntry(artist.id, EntityType.ARTIST, ListType.CHART, index, from.listeners)
        return TopListArtist(toplist, artist)
    }
}