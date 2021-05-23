package de.schnettler.repo.authentication.provider

import de.schnettler.database.daos.SessionDao
import de.schnettler.lastfm.LastFmAuthProvider
import javax.inject.Inject

class LastFmAuthProviderImpl @Inject constructor(
    private val dao: SessionDao
) : LastFmAuthProvider {
    val sessionLive = dao.getSession()

    suspend fun getSession() = dao.getSessionOnce()
    override suspend fun getSessionKey() = getSession()?.key.orEmpty()
}