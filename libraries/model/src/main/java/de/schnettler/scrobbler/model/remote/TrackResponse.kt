package de.schnettler.scrobbler.model.remote

interface TrackResponse {
    val name: String
    val url: String
    val artist: ArtistResponse
}