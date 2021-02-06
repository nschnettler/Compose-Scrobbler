package de.schnettler.repo.authentication.provider

import de.schnettler.database.daos.SessionDao
import de.schnettler.database.models.Session
import de.schnettler.lastfm.AuthProvider
import de.schnettler.lastfm.api.lastfm.SessionService
import de.schnettler.lastfm.api.lastfm.SessionService.Companion.METHOD_AUTH_SESSION
import de.schnettler.repo.mapping.auth.SessionMapper
import de.schnettler.repo.util.createSignature
import javax.inject.Inject

class LastFmAuthProvider @Inject constructor(
    private val service: SessionService,
    private val dao: SessionDao
) : AuthProvider {
    val sessionLive = dao.getSession()

    suspend fun refreshSession(token: String): Session {
        val params = mutableMapOf("token" to token, "method" to METHOD_AUTH_SESSION)
        val signature = createSignature(params)
        val session = SessionMapper.map(service.getSession(token, signature))
        dao.forceInsert(session)
        return session
    }

    suspend fun getSession() = dao.getSessionOnce()
    override suspend fun getSessionKey() = getSession()?.key.orEmpty()
}