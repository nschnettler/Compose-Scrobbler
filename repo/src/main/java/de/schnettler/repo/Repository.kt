package de.schnettler.repo

import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.nonFlowValueFetcher
import de.schnettler.database.models.Artist
import de.schnettler.lastfm.api.RetrofitService
import de.schnettler.repo.mapping.ArtistMapper
import de.schnettler.repo.mapping.forLists

class Repository {
    fun getTopArtists() = topArtistStore.stream(StoreRequest.fresh("1"))

    private val topArtistStore = StoreBuilder.from<String, List<Artist>>(
        fetcher = nonFlowValueFetcher {ArtistMapper.forLists().invoke(RetrofitService.lastFmService.getTopArtists().wrapper.artists)}
    ).build()
}