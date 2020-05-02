package de.schnettler.repo.mapping

import de.schnettler.database.models.Artist
import de.schnettler.database.models.Session
import de.schnettler.lastfm.models.ArtistDto
import de.schnettler.lastfm.models.SessionDto


object ArtistMapper : Mapper<ArtistDto, Artist> {
    override suspend fun map(from: ArtistDto): Artist = Artist(
            from.name,
            from.playcount,
            from.listeners,
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