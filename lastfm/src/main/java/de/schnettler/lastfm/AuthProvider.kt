package de.schnettler.lastfm


interface AuthProvider {
    suspend fun getSessionKey(): String
}