package de.schnettler.repo.mapping

import de.schnettler.database.models.Album
import de.schnettler.lastfm.models.AlbumDto
import de.schnettler.lastfm.models.AlbumInfoDto
import javax.inject.Inject

class AlbumInfoMapper @Inject constructor() : Mapper<AlbumInfoDto, Album> {
    override suspend fun map(from: AlbumInfoDto) = Album(
        name = from.name,
        url = from.url,
        plays = from.playcount,
        userPlays = from.userplaycount,
        imageUrl = from.image[3].url,
        listeners = from.listeners,
        artist = from.artist,
        tags = from.tags.tag.map { tag -> tag.name },
        description = from.wiki?.summary
    )
}

class AlbumMapper @Inject constructor() : Mapper<AlbumDto, Album> {
    override suspend fun map(from: AlbumDto) = Album(
        name = from.name,
        artist = from.artist.name,
        plays = from.playcount,
        url = from.url,
        imageUrl = from.images[3].url
    )
}