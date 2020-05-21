package de.schnettler.repo.mapping

import de.schnettler.database.models.Artist
import de.schnettler.database.models.ListEntry
import de.schnettler.database.models.ListEntryWithArtist
import de.schnettler.lastfm.models.ArtistDto

object TopListMapper: IndexedMapper<ArtistDto, ListEntryWithArtist> {
    override suspend fun map(index: Int, from: ArtistDto): ListEntryWithArtist {
        val artist = ArtistMinMapper.map(from)
        val entry = ListEntry(
            type = "TOP_LIST_ARTIST",
            index = index,
            id = from.name
        )
        return ListEntryWithArtist(entry, artist)
    }
}
