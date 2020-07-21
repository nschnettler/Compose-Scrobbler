package de.schnettler.repo

import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.nonFlowValueFetcher
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.mapping.map
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val service: LastFmService
) {
    val artistStore = StoreBuilder.from(
        fetcher = nonFlowValueFetcher {query: String ->
            service.searchArtist(query).map { it.map() }
        }
    ).build()
}