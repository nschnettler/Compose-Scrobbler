package de.schnettler.repo.mapping.artist

import de.schnettler.database.models.EntityType
import de.schnettler.database.models.ListType
import de.schnettler.database.models.TopListArtist
import de.schnettler.lastfm.models.UserArtistDto
import de.schnettler.repo.mapping.ArtistMapper
import de.schnettler.repo.mapping.IndexedMapper
import de.schnettler.repo.mapping.createTopListEntry
import javax.inject.Inject

class UserArtistMapper @Inject constructor() : IndexedMapper<UserArtistDto, TopListArtist> {
    override suspend fun map(index: Int, from: UserArtistDto): TopListArtist {
        val artist = ArtistMapper.map(from)
        val top = createTopListEntry(artist.id, EntityType.ARTIST, ListType.USER, index, from.playcount)
        return TopListArtist(top, artist)
    }
}