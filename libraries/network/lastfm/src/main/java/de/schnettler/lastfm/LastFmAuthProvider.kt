package de.schnettler.lastfm

interface LastFmAuthProvider {
    suspend fun getSessionKey(): String
}