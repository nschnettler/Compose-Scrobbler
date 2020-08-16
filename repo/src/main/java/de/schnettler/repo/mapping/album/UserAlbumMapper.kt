package de.schnettler.repo.mapping.album

import de.schnettler.database.models.EntityType
import de.schnettler.database.models.ListType
import de.schnettler.database.models.TopListAlbum
import de.schnettler.lastfm.models.AlbumDto
import de.schnettler.repo.mapping.AlbumMapper
import de.schnettler.repo.mapping.IndexedMapper
import de.schnettler.repo.mapping.createTopListEntry
import javax.inject.Inject

class UserAlbumMapper @Inject constructor() : IndexedMapper<AlbumDto, TopListAlbum> {
    override suspend fun map(index: Int, from: AlbumDto): TopListAlbum {
        val album = AlbumMapper.map(from)
        val top = createTopListEntry(album.id, EntityType.ALBUM, ListType.USER, index, from.playcount)
        return TopListAlbum(top, album)
    }
}