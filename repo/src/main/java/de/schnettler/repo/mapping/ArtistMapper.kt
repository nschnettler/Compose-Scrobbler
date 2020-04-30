package de.schnettler.repo.mapping

import de.schnettler.database.models.Artist
import de.schnettler.lastfm.models.ArtistDto
import java.text.NumberFormat
import java.util.*


object ArtistMapper : Mapper<ArtistDto, Artist> {
    override suspend fun map(from: ArtistDto): Artist {
        val nf: NumberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        nf.isGroupingUsed = true
        return Artist(
            from.name,
            nf.format(from.playcount),
            nf.format(from.listeners),
            from.mbid,
            from.url,
            from.streamable)
    }


}