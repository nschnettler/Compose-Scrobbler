package de.schnettler.repo.mapping

import de.schnettler.database.models.Album
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
).apply { tracks = this@map.tracks.track.mapIndexed{ index, track ->
    track.map(this@map.name, index) }
}