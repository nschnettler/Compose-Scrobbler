package de.schnettler.repo.mapping

import de.schnettler.database.models.EntityWithStats
import de.schnettler.database.models.LastFmEntity.Album
import de.schnettler.database.models.LastFmEntity.Artist
import de.schnettler.database.models.LastFmEntity.Track
import de.schnettler.database.models.Stats
import de.schnettler.lastfm.models.SearchResultDto

fun SearchResultDto.mapToAlbum() = Album(
    name = name,
    artist = artist,
    url = url
)

fun SearchResultDto.mapToTrack() = Track(
    name = name,
    artist = artist,
    url = url
)

fun SearchResultDto.mapToArtist(): EntityWithStats.ArtistWithStats {
    val artist = Artist(
        name = name,
        url = url
    )
    return EntityWithStats.ArtistWithStats(artist, Stats(id = artist.id, listeners = listeners))
}