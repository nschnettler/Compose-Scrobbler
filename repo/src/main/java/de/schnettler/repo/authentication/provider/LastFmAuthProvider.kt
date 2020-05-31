package de.schnettler.repo.authentication.provider

import com.dropbox.android.external.store4.*
import de.schnettler.database.daos.AuthDao
import de.schnettler.database.models.Session
import de.schnettler.lastfm.api.LastFmService
import de.schnettler.repo.mapping.SessionMapper
import de.schnettler.repo.util.createSignature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LastFmAuthProvider(private val service: LastFmService, private val dao: AuthDao, scope: CoroutineScope) {
    lateinit var session: Session
    fun getObservableSession() = lastFmSessionStore.stream(StoreRequest.cached("", false))
    suspend fun getSession() = lastFmSessionStore.get("")
    suspend fun refreshSession(token: String) =  lastFmSessionStore.fresh(token)

    private val lastFmSessionStore = StoreBuilder.from (
        fetcher = nonFlowValueFetcher {token: String ->
            val params= mutableMapOf("token" to token)
            val signature = createSignature(LastFmService.METHOD_AUTH_SESSION, params, LastFmService.SECRET)

            SessionMapper.map(service.getSession(token, signature))
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = { _: String ->
                dao.getSession()
            },
            writer = { _: String, session ->
                dao.insertSession(session)
            }
        )
    ).build()

    init {
        scope.launch(Dispatchers.IO) {
            session = getSession()
        }
    }
}