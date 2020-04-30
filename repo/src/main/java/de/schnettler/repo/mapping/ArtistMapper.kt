package de.schnettler.repo.mapping

import de.schnettler.database.models.Artist
import de.schnettler.lastfm.models.ArtistDto
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


object ArtistMapper : Mapper<ArtistDto, Artist> {
    override suspend fun map(from: ArtistDto) = Artist(
            from.name,
            from.playcount,
            from.listeners,
            from.mbid,
            from.url,
            from.streamable)
}