package de.schnettler.scrobbler.core.map

import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.model.remote.AlbumResponse

object AlbumMapper : Mapper<AlbumResponse, LastFmEntity.Album> {
    override suspend fun map(from: AlbumResponse) = LastFmEntity.Album(
        name = from.name,
        url = from.url,
        artist = from.artist,
        imageUrl = from.images.lastOrNull()?.url
    )
}