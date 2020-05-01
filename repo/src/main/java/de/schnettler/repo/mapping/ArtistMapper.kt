package de.schnettler.repo.mapping

import de.schnettler.database.models.Artist
import de.schnettler.lastfm.models.ArtistDto


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