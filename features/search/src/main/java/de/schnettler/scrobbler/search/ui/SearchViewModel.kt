package de.schnettler.scrobbler.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.StoreRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.scrobbler.core.ktx.update
import de.schnettler.scrobbler.core.model.BaseEntity
import de.schnettler.scrobbler.core.ui.state.RefreshableUiState
import de.schnettler.scrobbler.search.domain.SearchRepository
import de.schnettler.scrobbler.search.model.SearchQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repo: SearchRepository) : ViewModel() {
    private val _searchQuery: MutableStateFlow<SearchQuery> = MutableStateFlow(SearchQuery("", 0))
    val searchQuery: StateFlow<SearchQuery>
        get() = _searchQuery
    val state: MutableStateFlow<RefreshableUiState<List<BaseEntity>>> =
        MutableStateFlow(RefreshableUiState.Success(null, true))

    fun updateQuery(new: String) {
        if (new != searchQuery.value.query) {
            _searchQuery.value = searchQuery.value.copy(query = new)
        }
    }

    fun updateFilter(filter: Int) {
        if (filter != searchQuery.value.filter) {
            _searchQuery.value = searchQuery.value.copy(filter = filter)
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