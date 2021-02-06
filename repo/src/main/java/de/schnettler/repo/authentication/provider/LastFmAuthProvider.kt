package de.schnettler.repo.authentication.provider

import de.schnettler.database.daos.SessionDao
import de.schnettler.lastfm.AuthProvider
import javax.inject.Inject

class LastFmAuthProvider @Inject constructor(
    private val dao: SessionDao
) : AuthProvider {
    val sessionLive = dao.getSession()

    suspend fun getSession() = dao.getSessionOnce()
    override suspend fun getSessionKey() = getSession()?.key.orEmpty()
}