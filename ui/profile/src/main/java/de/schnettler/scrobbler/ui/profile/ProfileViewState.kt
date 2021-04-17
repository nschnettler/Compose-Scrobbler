package de.schnettler.scrobbler.ui.profile

import de.schnettler.scrobbler.core.model.TopListAlbum
import de.schnettler.scrobbler.core.model.TopListArtist
import de.schnettler.scrobbler.core.model.TopListTrack
import de.schnettler.scrobbler.core.model.User

data class ProfileViewState(
    val user: User? = null,
    val topArtists: List<TopListArtist> = emptyList(),
    val topAlbums: List<TopListAlbum> = emptyList(),
    val topTracks: List<TopListTrack> = emptyList(),
)