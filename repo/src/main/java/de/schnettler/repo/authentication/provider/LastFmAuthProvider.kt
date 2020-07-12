package de.schnettler.repo.authentication.provider

import de.schnettler.database.daos.AuthDao
import de.schnettler.database.models.Session
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.mapping.SessionMapper
import de.schnettler.repo.util.createSignature
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class LastFmAuthProvider @Inject constructor(
    private val service: LastFmService,
    private val dao: AuthDao
) {
    var session: Session? = null
    val sessionLive = dao.getSession()

    suspend fun refreshSession(token: String): Session {
        val params= mutableMapOf("token" to token)
        val signature = createSignature(LastFmService.METHOD_AUTH_SESSION, params, LastFmService.SECRET)
        val session = SessionMapper.map(service.getSession(token, signature))
        dao.insertSession(session)
        return session
    }

    fun getSessionOrThrow() = session ?: throw Exception("Session was null")
    fun getSessionKeyOrThrow() = session?.key ?: throw Exception("Session was null")

    fun loggedIn() = session != null

    init {
        GlobalScope.launch(Dispatchers.IO) {
            sessionLive.collect {
                session = it
            }
        }
    }
}