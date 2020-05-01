package de.schnettler.repo.mapping

import de.schnettler.database.models.Artist
import de.schnettler.database.models.ImageUrls
import de.schnettler.lastfm.models.ArtistDto


object ArtistMapper : Mapper<ArtistDto, Artist> {
    override suspend fun map(from: ArtistDto): Artist = Artist(
            from.name,
            from.playcount,
            from.listeners,
            from.mbid,
            from.url,
            from.streamable,
            ImageUrls(from.images[0].url, from.images[1].url, from.images[2].url, from.images[3].url, from.images[4].url)
    )
}