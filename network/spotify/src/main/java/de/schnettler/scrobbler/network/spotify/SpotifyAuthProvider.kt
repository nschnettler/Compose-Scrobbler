package de.schnettler.scrobbler.network.spotify


interface SpotifyAuthProvider {
    suspend fun getAuthToken(): String
    suspend fun refreshToken(): String
}