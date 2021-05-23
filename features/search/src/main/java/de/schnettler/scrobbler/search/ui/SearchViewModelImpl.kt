package de.schnettler.scrobbler.search.ui

import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.StoreRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.scrobbler.core.ktx.update
import de.schnettler.scrobbler.core.ui.state.RefreshableUiState
import de.schnettler.scrobbler.search.domain.SearchRepository
import de.schnettler.scrobbler.search.model.SearchQuery
import de.schnettler.scrobbler.search.model.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModelImpl @Inject constructor(private val repo: SearchRepository) : SearchViewModel() {
    override val searchQuery: MutableStateFlow<SearchQuery> = MutableStateFlow(SearchQuery("", 0))

    override val state: MutableStateFlow<RefreshableUiState<List<SearchResult>>> =
        MutableStateFlow(RefreshableUiState.Success(null, true))

    override fun updateQuery(new: String) {
        if (new != searchQuery.value.query) {
            searchQuery.value = searchQuery.value.copy(query = new)
        }
    }

    override fun updateFilter(filter: Int) {
        if (filter != searchQuery.value.filter) {
            searchQuery.value = searchQuery.value.copy(filter = filter)
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            searchQuery.debounce(300)
                .filter { it.query.isNotBlank() }
                .flatMapLatest { query ->
                    Timber.d("Filter: ${query.filter}")
                    repo.artistStore.stream(StoreRequest.fresh(query))
                }.collect {
                    state.update(it)
                }
        }
    }
}