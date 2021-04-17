package de.schnettler.scrobbler.search.domain

import de.schnettler.scrobbler.search.model.SearchResult.AlbumResult
import de.schnettler.scrobbler.search.model.SearchResult.ArtistResult
import de.schnettler.scrobbler.search.model.SearchResult.TrackResult
import de.schnettler.scrobbler.search.model.SearchResultResponse
import javax.inject.Inject

class SearchResultResponseMapper @Inject constructor() {
    fun mapToArtist(from: SearchResultResponse): ArtistResult = ArtistResult(
        name = from.name,
        listeners = from.listeners,
    )

    fun mapToTrack(from: SearchResultResponse): TrackResult = TrackResult(
        name = from.name,
        artistName = from.artist,
    )

    fun mapToAlbum(from: SearchResultResponse): AlbumResult = AlbumResult(
        name = from.name,
        artistName = from.artist
    )
}