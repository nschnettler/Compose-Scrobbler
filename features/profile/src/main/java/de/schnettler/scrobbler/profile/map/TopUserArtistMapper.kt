package de.schnettler.scrobbler.profile.map

import de.schnettler.scrobbler.core.map.ArtistMapper
import de.schnettler.scrobbler.core.map.IndexedMapper
import de.schnettler.scrobbler.core.map.createTopListEntry
import de.schnettler.scrobbler.model.EntityType
import de.schnettler.scrobbler.model.ListType
import de.schnettler.scrobbler.model.TopListArtist
import de.schnettler.scrobbler.profile.model.remote.TopArtistResponse

object TopUserArtistMapper : IndexedMapper<TopArtistResponse, TopListArtist> {
    override suspend fun map(index: Int, from: TopArtistResponse): TopListArtist {
        val artist = ArtistMapper.map(from)
        val top = createTopListEntry(artist.id, EntityType.ARTIST, ListType.USER, index, from.playcount)
        return TopListArtist(top, artist)
    }
}