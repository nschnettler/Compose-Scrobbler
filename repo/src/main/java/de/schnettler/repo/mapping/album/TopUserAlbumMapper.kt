package de.schnettler.repo.mapping.album

import de.schnettler.scrobbler.core.model.TopListAlbum
import de.schnettler.lastfm.models.AlbumDto
import de.schnettler.scrobbler.core.map.AlbumMapper
import de.schnettler.scrobbler.core.map.IndexedMapper
import de.schnettler.scrobbler.core.map.createTopListEntry
import de.schnettler.scrobbler.core.model.EntityType
import de.schnettler.scrobbler.core.model.ListType

object TopUserAlbumMapper : IndexedMapper<AlbumDto, TopListAlbum> {
    override suspend fun map(index: Int, from: AlbumDto): TopListAlbum {
        val album = AlbumMapper.map(from)
        val top = createTopListEntry(album.id, EntityType.ALBUM, ListType.USER, index, from.playcount)
        return TopListAlbum(top, album)
    }
}