package de.schnettler.scrobbler.model.remote

interface AlbumResponse {
    val name: String
    val url: String
    val artist: String
    val images: List<ImageResponse>
}