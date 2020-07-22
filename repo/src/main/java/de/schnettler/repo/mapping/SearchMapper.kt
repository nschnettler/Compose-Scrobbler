package de.schnettler.repo.mapping

import de.schnettler.database.models.Album
import de.schnettler.database.models.Artist
import de.schnettler.database.models.Track
import de.schnettler.lastfm.models.SearchResultDto

fun SearchResultDto.mapToAlbum() = Album(
    name = name,
    artist = artist,
    url = url
)

fun SearchResultDto.mapToTrack() = Track(
    name = name,
    artist = artist,
    url = url,
    listeners = listeners
)

fun SearchResultDto.mapToArtist() = Artist(
    name = name,
    url = url,
    listeners = listeners
)