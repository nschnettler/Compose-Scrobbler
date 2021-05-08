package de.schnettler.scrobbler.profile.map

import de.schnettler.scrobbler.core.map.AlbumMapper
import de.schnettler.scrobbler.core.map.IndexedMapper
import de.schnettler.scrobbler.core.map.createTopListEntry
import de.schnettler.scrobbler.model.EntityType
import de.schnettler.scrobbler.model.ListType
import de.schnettler.scrobbler.model.TopListAlbum
import de.schnettler.scrobbler.profile.model.remote.TopAlbumResponse

object TopUserAlbumMapper : IndexedMapper<TopAlbumResponse, TopListAlbum> {
    override suspend fun map(index: Int, from: TopAlbumResponse): TopListAlbum {
        val album = AlbumMapper.map(from)
        val top = createTopListEntry(album.id, EntityType.ALBUM, ListType.USER, index, from.playcount)
        return TopListAlbum(top, album)
    }
}