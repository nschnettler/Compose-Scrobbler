package de.schnettler.repo

import android.content.Context
import com.dropbox.android.external.store4.*
import de.schnettler.database.models.Artist
import de.schnettler.database.provideDatabase
import de.schnettler.lastfm.api.LastFmService
import de.schnettler.lastfm.api.RetrofitService
import de.schnettler.repo.mapping.ArtistMapper
import de.schnettler.repo.mapping.SessionMapper
import de.schnettler.repo.mapping.forLists
import de.schnettler.repo.util.md5
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

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
        val signature = "api_key${LastFmService.API_KEY}method${LastFmService.METHOD_SESSION}token$token${LastFmService.SECRET}".md5()
        val session = SessionMapper.map(service.getSession(token, LastFmService.API_KEY, signature))
        db.authDao().insertSession(session)
    }

    fun getSession() = db.authDao().getSession()
}


