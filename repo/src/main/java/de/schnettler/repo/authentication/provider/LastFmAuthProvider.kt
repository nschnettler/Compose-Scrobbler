package de.schnettler.repo.authentication.provider

import de.schnettler.database.daos.AuthDao
import de.schnettler.database.models.Session
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.lastfm.api.lastfm.LastFmService.Companion.METHOD_AUTH_SESSION
import de.schnettler.repo.mapping.SessionMapper
import de.schnettler.repo.util.createSignature
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoSessionFoundException(message: String) : Exception(message)

class LastFmAuthProvider @Inject constructor(
    private val service: LastFmService,
    private val dao: AuthDao
) {
    var session: Session? = null
    val sessionLive = dao.getSession()

    suspend fun refreshSession(token: String): Session {
        val params = mutableMapOf("token" to token, "method" to METHOD_AUTH_SESSION)
        val signature = createSignature(params)
        val session = SessionMapper.map(service.getSession(token, signature))
        dao.insertSession(session)
        return session
    }

    fun getSessionOrThrow() = session ?: throw NoSessionFoundException("Session was null. Reauthorise the user")
    fun getSessionKeyOrThrow() = session?.key ?: throw NoSessionFoundException("Session was null. Reauthorise the user")

    fun loggedIn() = session != null

    init {
        GlobalScope.launch(Dispatchers.IO) {
            sessionLive.collect {
                session = it
            }
        }
    }
}