package de.schnettler.repo.mapping

import de.schnettler.database.models.Album
import de.schnettler.lastfm.models.AlbumDto
import de.schnettler.lastfm.models.AlbumInfoDto

suspend fun AlbumInfoDto.map() = Album(
    name = name,
    url = url,
    plays = playcount,
    userPlays = userplaycount,
    imageUrl = image[3].url,
    listeners = listeners,
    artist = artist,
    tags = tags.tag.map { tag -> tag.name },
    description = wiki?.summary
).apply {
    tracks = this@map.tracks.track.mapIndexed { index, track ->
        track.map(this@map.name, index)
    }
}

fun AlbumDto.mapToUserAlbum() = Album(
    name = this.name,
    artist = this.artist.name,
    userPlays = this.playcount,
    url = this.url,
    imageUrl = this.images[3].url
)

fun AlbumDto.mapToAlbum() = Album(
    name = this.name,
    artist = this.artist.name,
    plays = this.playcount,
    url = this.url,
    imageUrl = this.images[3].url
)