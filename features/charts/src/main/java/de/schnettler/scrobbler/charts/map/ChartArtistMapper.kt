package de.schnettler.scrobbler.charts.map

import de.schnettler.scrobbler.charts.model.ChartArtistResponse
import de.schnettler.scrobbler.core.map.ArtistMapper
import de.schnettler.scrobbler.core.map.IndexedMapper
import de.schnettler.scrobbler.core.map.createTopListEntry
import de.schnettler.scrobbler.model.EntityType
import de.schnettler.scrobbler.model.ListType
import de.schnettler.scrobbler.model.TopListArtist

object ChartArtistMapper : IndexedMapper<ChartArtistResponse, TopListArtist> {
    override suspend fun map(index: Int, from: ChartArtistResponse): TopListArtist {
        val artist = ArtistMapper.map(from)
        val toplist = createTopListEntry(artist.id, EntityType.ARTIST, ListType.CHART, index, from.listeners)
        return TopListArtist(toplist, artist)
    }
}