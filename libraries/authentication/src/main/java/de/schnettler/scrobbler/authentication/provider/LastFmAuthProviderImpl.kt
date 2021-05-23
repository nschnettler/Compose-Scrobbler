package de.schnettler.scrobbler.authentication.provider

import de.schnettler.lastfm.LastFmAuthProvider
import de.schnettler.scrobbler.authentication.db.SessionDao
import javax.inject.Inject

class LastFmAuthProviderImpl @Inject constructor(
    private val dao: SessionDao
) : LastFmAuthProvider {
    val sessionLive = dao.getSession()

    override suspend fun getSessionKey() = getSession()?.key.orEmpty()
    suspend fun getSession() = dao.getSessionOnce()
}