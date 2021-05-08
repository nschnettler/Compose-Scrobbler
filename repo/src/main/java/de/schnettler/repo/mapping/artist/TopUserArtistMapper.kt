package de.schnettler.repo.mapping.artist

import de.schnettler.lastfm.models.UserArtistDto
import de.schnettler.scrobbler.core.map.ArtistMapper
import de.schnettler.scrobbler.core.map.IndexedMapper
import de.schnettler.scrobbler.core.map.createTopListEntry
import de.schnettler.scrobbler.model.EntityType
import de.schnettler.scrobbler.model.ListType
import de.schnettler.scrobbler.model.TopListArtist

object TopUserArtistMapper : IndexedMapper<UserArtistDto, TopListArtist> {
    override suspend fun map(index: Int, from: UserArtistDto): TopListArtist {
        val artist = ArtistMapper.map(from)
        val top = createTopListEntry(artist.id, EntityType.ARTIST, ListType.USER, index, from.playcount)
        return TopListArtist(top, artist)
    }
}