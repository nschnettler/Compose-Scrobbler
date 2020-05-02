package de.schnettler.repo

import android.content.Context
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.nonFlowValueFetcher
import de.schnettler.database.models.Artist
import de.schnettler.database.models.User
import de.schnettler.database.provideDatabase
import de.schnettler.lastfm.api.LastFmService
import de.schnettler.lastfm.api.RetrofitService
import de.schnettler.lastfm.models.UserDto
import de.schnettler.repo.mapping.ArtistMapper
import de.schnettler.repo.mapping.SessionMapper
import de.schnettler.repo.mapping.UserMapper
import de.schnettler.repo.mapping.forLists
import de.schnettler.repo.util.md5
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import java.util.*
import kotlin.collections.List
import kotlin.collections.MutableMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.collections.set

@ExperimentalCoroutinesApi
@FlowPreview
class Repository(context: Context) {

    private val service = RetrofitService.lastFmService
    private val db = provideDatabase(context)

    fun getTopArtists() = topArtistStore.stream(StoreRequest.fresh("1"))

    private val topArtistStore = StoreBuilder.from<String, List<Artist>>(
        fetcher = nonFlowValueFetcher {
            ArtistMapper.forLists().invoke(service.getTopArtists())
        }
    ).build()

    suspend fun refreshSession(token: String) {
        val params= mutableMapOf("token" to token)
        val signature = createSignature(LastFmService.METHOD_AUTH_SESSION, params, LastFmService.SECRET)
        val session = SessionMapper.map(service.getSession(token, signature))
        db.authDao().insertSession(session)
    }

    fun getSession() = db.authDao().getSession()


    fun getUserInfo(sessionKey: String): Flow<StoreResponse<User>> {
        val userInfoStore = StoreBuilder.from<String, User>(
            fetcher = nonFlowValueFetcher {
                UserMapper.map(service.getUserInfo(sessionKey))
            }
        ).build()
        return userInfoStore.stream(StoreRequest.fresh(""))
    }
}

fun createSignature(method: String, params: MutableMap<String, String>, secret: String): String {
    params["method"] = method
    params["api_key"] = LastFmService.API_KEY
    val sorted = params.toSortedMap()
    val signature = StringBuilder()
    sorted.forEach { (key, value) ->
        signature.append(key)
        signature.append(value)
    }
    signature.append(secret)
    return signature.toString().md5()
}