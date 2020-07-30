package de.schnettler.repo.mapping

import de.schnettler.database.models.Album
import de.schnettler.database.models.Artist
import de.schnettler.database.models.Track
import de.schnettler.lastfm.models.AlbumDto
import de.schnettler.lastfm.models.UserArtistDto
import de.schnettler.lastfm.models.UserTrackDto
import javax.inject.Inject

class UserAlbumMapper @Inject constructor() : Mapper<AlbumDto, Album> {
    override suspend fun map(from: AlbumDto) = Album(
        name = from.name,
        artist = from.artist.name,
        userPlays = from.playcount,
        url = from.url,
        imageUrl = from.images[3].url
    )
}

class UserArtistMapper @Inject constructor() : Mapper<UserArtistDto, Artist> {
    override suspend fun map(from: UserArtistDto) = Artist(
        name = from.name,
        url = from.url,
        userPlays = from.playcount ?: 0
    )
}

class UserTrackMapper @Inject constructor() : Mapper<UserTrackDto, Track> {
    override suspend fun map(from: UserTrackDto) = Track(
        name = from.name,
        url = from.url,
        duration = from.duration,
        artist = from.artist.name,
        userPlays = from.playcount
    )
}