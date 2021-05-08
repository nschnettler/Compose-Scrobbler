package de.schnettler.scrobbler.core.map

import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.model.remote.ArtistResponse

object ArtistMapper : Mapper<ArtistResponse, LastFmEntity.Artist> {
    override suspend fun map(from: ArtistResponse) = LastFmEntity.Artist(
        name = from.name,
        url = from.url
    )
}