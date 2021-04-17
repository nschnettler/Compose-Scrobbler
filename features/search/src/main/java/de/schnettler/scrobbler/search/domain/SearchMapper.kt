package de.schnettler.scrobbler.search.domain

import de.schnettler.scrobbler.core.model.EntityWithStats
import de.schnettler.scrobbler.core.model.LastFmEntity
import de.schnettler.scrobbler.core.model.Stats
import de.schnettler.scrobbler.search.model.SearchResultResponse

fun SearchResultResponse.mapToAlbum() = LastFmEntity.Album(
    name = name,
    artist = artist,
    url = url
)

fun SearchResultResponse.mapToTrack() = LastFmEntity.Track(
    name = name,
    artist = artist,
    url = url
)

fun SearchResultResponse.mapToArtist(): EntityWithStats.ArtistWithStats {
    val artist = LastFmEntity.Artist(
        name = name,
        url = url
    )
    return EntityWithStats.ArtistWithStats(artist, Stats(id = artist.id, listeners = listeners))
}