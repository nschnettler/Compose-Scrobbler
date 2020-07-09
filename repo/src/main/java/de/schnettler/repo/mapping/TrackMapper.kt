package de.schnettler.repo.mapping

import de.schnettler.database.models.ScrobbleStatus
import de.schnettler.database.models.Track
import de.schnettler.lastfm.models.*
import de.schnettler.repo.util.toBoolean

fun UserTrackDto.map() = Track(
    name = this.name,
    url = this.url,
    duration = this.duration,
    artist = this.artist.name,
    userPlays = this.playcount
)

fun ArtistTracksDto.map() = Track(
    name = this.name,
    url = this.url,
    listeners = this.listeners,
    plays = this.playcount,
    artist = this.artist.name
)

fun RecentTracksDto.map() = Track(
    name = this.name,
    url = this.url,
    artist = this.artist.name,
    album = this.album.name
).apply {
    timestamp = this@map.date?.asMs() ?: 0
    scrobbleStatus = if (attrs?.nowplaying?.toBoolean() == true) ScrobbleStatus.PLAYING else ScrobbleStatus.SCROBBLED
}

fun TrackInfoDto.map() = Track(
    name = this.name,
    url = this.url,
    duration = this.duration,
    listeners = this.listeners,
    plays = this.playcount,
    artist = this.artist.name,
    album = this.album?.title,
    userPlays = this.userplaycount ?: 0,
    userLoved = this.userloved.toBoolean(),
    tags = this.toptags.tag.map { tag -> tag.name }
)

suspend fun AlbumTrack.map(albumName: String) = Track(
    name = name,
    url = url,
    duration = duration,
    artist = artist.name,
    album = albumName
)