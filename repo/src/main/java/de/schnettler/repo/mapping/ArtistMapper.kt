package de.schnettler.repo.mapping

import de.schnettler.database.models.Artist
import de.schnettler.database.models.Session
import de.schnettler.database.models.Track
import de.schnettler.database.models.User
import de.schnettler.lastfm.models.ArtistDto
import de.schnettler.lastfm.models.SessionDto
import de.schnettler.lastfm.models.TrackDto
import de.schnettler.lastfm.models.UserDto


object ArtistMapper : Mapper<ArtistDto, Artist> {
    override suspend fun map(from: ArtistDto): Artist = Artist(
            from.name,
            from.playcount,
            from.listeners ?: 0,
            from.mbid,
            from.url,
            from.streamable
    )
}

object SessionMapper: Mapper<SessionDto, Session> {
    override suspend fun map(from: SessionDto): Session = Session(
        from.name,
        from.key,
        System.currentTimeMillis()
    )
}

object UserMapper: Mapper<UserDto, User> {
    override suspend fun map(from: UserDto): User {
        val user = User(
            from.name,
            from.playcount,
            from.url,
            from.country,
            from.age,
            from.realname,
            from.registerDate.unixtime
        )
        return user
    }
}

object TrackMapper: Mapper<TrackDto, Track> {
    override suspend fun map(from: TrackDto) = Track(
        name = from.name,
        id = from.mbid,
        album = from.album.name,
        artist = from.artist.name
    )
}