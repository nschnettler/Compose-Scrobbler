package de.schnettler.repo.mapping.artist

import de.schnettler.database.models.LastFmEntity.Artist
import de.schnettler.lastfm.models.MinimalArtist
import de.schnettler.repo.mapping.BaseArtistMapper
import de.schnettler.repo.mapping.Mapper
import javax.inject.Inject

class MinimalArtistMapper @Inject constructor() : Mapper<MinimalArtist, Artist> {
    override suspend fun map(from: MinimalArtist) = BaseArtistMapper.map(from)
}