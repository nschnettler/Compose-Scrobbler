package de.schnettler.scrobbler.compose.model

import de.schnettler.scrobbler.compose.navigation.Screen

sealed class NavigationEvent {
    sealed class OpenScreen : NavigationEvent() {
        abstract val navAction: String
        data class OpenArtistDetails(val name: String) : OpenScreen() {
            override val navAction = Screen.ArtistDetails.withArg(name)
        }
        data class OpenAlbumDetails(val name: String, val artistName: String) : OpenScreen() {
            override val navAction = Screen.AlbumDetails.withArgs(listOf(artistName, name))
        }
        data class OpenTrackDetails(val name: String, val artistName: String) : OpenScreen() {
            override val navAction = Screen.TrackDetails.withArgs(listOf(artistName, name))
        }
    }

    data class OpenUrlInBrowser(val url: String) : NavigationEvent()
    object OpenNotificationListenerSettings : NavigationEvent()
}