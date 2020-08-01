package de.schnettler.repo.mapping

import de.schnettler.database.models.LastFmEntity.Artist
import de.schnettler.lastfm.models.MinimalListing
import javax.inject.Inject

class EntityMapper @Inject constructor() : Mapper<MinimalListing, Artist> {
    override suspend fun map(from: MinimalListing) =
        Artist(name = from.name, url = from.url)
}