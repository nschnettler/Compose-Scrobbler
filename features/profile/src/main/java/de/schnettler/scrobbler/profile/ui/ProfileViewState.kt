package de.schnettler.scrobbler.profile.ui

import de.schnettler.scrobbler.model.TopListAlbum
import de.schnettler.scrobbler.model.TopListArtist
import de.schnettler.scrobbler.model.TopListTrack
import de.schnettler.scrobbler.model.User

data class ProfileViewState(
    val user: User? = null,
    val topArtists: List<TopListArtist> = emptyList(),
    val topAlbums: List<TopListAlbum> = emptyList(),
    val topTracks: List<TopListTrack> = emptyList(),
)