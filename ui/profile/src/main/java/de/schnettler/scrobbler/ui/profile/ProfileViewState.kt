package de.schnettler.scrobbler.ui.profile

import de.schnettler.database.models.TopListAlbum
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListTrack
import de.schnettler.database.models.User

data class ProfileViewState(
    val user: User? = null,
    val topArtists: List<TopListArtist> = emptyList(),
    val topAlbums: List<TopListAlbum> = emptyList(),
    val topTracks: List<TopListTrack> = emptyList(),
)