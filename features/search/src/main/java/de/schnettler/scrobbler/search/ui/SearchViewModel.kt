package de.schnettler.scrobbler.search.ui

import androidx.lifecycle.ViewModel
import de.schnettler.scrobbler.core.ui.state.RefreshableUiState
import de.schnettler.scrobbler.search.model.SearchQuery
import de.schnettler.scrobbler.search.model.SearchResult
import kotlinx.coroutines.flow.StateFlow

abstract class SearchViewModel : ViewModel() {
    abstract val searchQuery: StateFlow<SearchQuery>
    abstract val state: StateFlow<RefreshableUiState<List<SearchResult>>>
    abstract fun updateQuery(new: String)
    abstract fun updateFilter(filter: Int)
}