package de.schnettler.scrobbler.search.domain

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.scrobbler.search.api.SearchApi
import de.schnettler.scrobbler.search.model.SearchQuery
import de.schnettler.scrobbler.search.model.SearchResult
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val searchApi: SearchApi,
    private val searchResultResponseMapper: SearchResultResponseMapper,
) {
    val artistStore: Store<SearchQuery, List<SearchResult>> = StoreBuilder.from(
        fetcher = Fetcher.of { query: SearchQuery ->
            when (query.filter) {
                1 -> searchApi.searchArtist(query.query, 30).map(searchResultResponseMapper::mapToArtist)
                2 -> searchApi.searchAlbum(query.query, 30).map(searchResultResponseMapper::mapToAlbum)
                3 -> searchApi.searchTrack(query.query, 30).map(searchResultResponseMapper::mapToTrack)
                else -> searchApi.searchArtist(query.query, 3).map(searchResultResponseMapper::mapToArtist) +
                    searchApi.searchAlbum(query.query, 3).map(searchResultResponseMapper::mapToAlbum) +
                    searchApi.searchTrack(query.query, 3).map(searchResultResponseMapper::mapToTrack)
            }
        }
    ).build()
}