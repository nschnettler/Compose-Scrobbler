package de.schnettler.scrobbler.ui.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.StoreRequest
import de.schnettler.database.models.BaseEntity
import de.schnettler.repo.SearchQuery
import de.schnettler.repo.SearchRepository
import de.schnettler.scrobbler.ui.common.compose.RefreshableUiState
import de.schnettler.scrobbler.ui.common.compose.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewModel @ViewModelInject constructor(private val repo: SearchRepository) : ViewModel() {
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