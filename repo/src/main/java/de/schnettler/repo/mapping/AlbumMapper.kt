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
    artist = artist
).apply { tracks = this@map.tracks.track.map { it.map(this@map.name) } }