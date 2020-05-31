package de.schnettler.repo.authentication.provider

import de.schnettler.database.daos.AuthDao
import de.schnettler.database.models.Session
import de.schnettler.lastfm.api.LastFmService
import de.schnettler.repo.mapping.SessionMapper
import de.schnettler.repo.util.createSignature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class LastFmAuthProvider(private val service: LastFmService, private val dao: AuthDao, scope: CoroutineScope) {
    var session: Session? = null
    val sessionLive = dao.getSession()

    suspend fun refreshSession(token: String): Session {
        val params= mutableMapOf("token" to token)
        val signature = createSignature(LastFmService.METHOD_AUTH_SESSION, params, LastFmService.SECRET)
        val session = SessionMapper.map(service.getSession(token, signature))
        dao.insertSession(session)
        return session
    }

    init {
        scope.launch(Dispatchers.IO) {
            sessionLive.collect {
                session = it
            }
        }
    }
}