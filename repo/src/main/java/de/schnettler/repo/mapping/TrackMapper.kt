package de.schnettler.repo.mapping

import de.schnettler.database.models.LocalTrack
import de.schnettler.database.models.ScrobbleStatus
import de.schnettler.database.models.Track
import de.schnettler.lastfm.models.AlbumTrack
import de.schnettler.lastfm.models.ArtistTracksDto
import de.schnettler.lastfm.models.RecentTracksDto
import de.schnettler.lastfm.models.TrackInfoDto
import de.schnettler.lastfm.models.UserTrackDto
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

fun RecentTracksDto.mapToLocal() = LocalTrack(
    name = name,
    artist = artist.name,
    album = album.name,
    duration = 1,
    timestamp = date?.uts ?: -1,
    playedBy = "external",
    status = if (date != null) ScrobbleStatus.EXTERNAL else ScrobbleStatus.PLAYING
)

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

suspend fun AlbumTrack.map(albumName: String, index: Int) = Track(
    name = name,
    url = url,
    duration = duration,
    artist = artist.name,
    album = albumName,
    rank = index + 1
)