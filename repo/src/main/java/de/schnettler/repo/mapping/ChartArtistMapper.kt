package de.schnettler.repo.mapping

import de.schnettler.database.models.EntityType
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.ListType
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListEntry
import de.schnettler.lastfm.models.ChartArtistDto
import javax.inject.Inject

class ChartArtistMapper @Inject constructor() : IndexedMapper<ChartArtistDto, TopListArtist> {
    override suspend fun map(index: Int, from: ChartArtistDto): TopListArtist {
        val artist = LastFmEntity.Artist(
            name = from.name,
            url = from.url
        )
        val toplist = TopListEntry(
            id = artist.id,
            entityType = EntityType.ARTIST,
            listType = ListType.CHART,
            index = index,
            count = from.listeners ?: 0
        )
        return TopListArtist(toplist, artist)
    }
}