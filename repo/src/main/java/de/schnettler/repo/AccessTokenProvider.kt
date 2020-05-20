package de.schnettler.repo

import de.schnettler.database.daos.AuthDao
import de.schnettler.database.models.AuthToken
import de.schnettler.database.models.AuthTokenType

class AccessTokenProvider(private val repo: Repository, private val authDao: AuthDao) {
    fun getToken() = authDao.getAuthToken(AuthTokenType.Spotify.value)

    suspend fun getNonNullToken(): AuthToken {
        val token = getToken()
        println("Requested Token. Result: $token")
        return token ?: refreshToken()
    }

    suspend fun refreshToken(): AuthToken {
        println("Refreshing Token")
        val newToken = repo.refreshSpotifyAuthToken()
        repo.insertAuthToken(newToken)
        return newToken
    }
}