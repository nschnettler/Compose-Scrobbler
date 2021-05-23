package de.schnettler.scrobbler.search.model

sealed class SearchResult {
    data class ArtistResult(
        val name: String,
        val listeners: Long,
    ) : SearchResult()

    data class AlbumResult(
        val name: String,
        val artistName: String,
    ) : SearchResult()

    data class TrackResult(
        val name: String,
        val artistName: String,
    ) : SearchResult()
}