package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.StoreRequest
import de.schnettler.database.models.LastFmStatsEntity
import de.schnettler.repo.SearchRepository
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.scrobbler.util.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class SearchViewModel @ViewModelInject constructor(private val repo: SearchRepository): ViewModel() {
    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    val query: StateFlow<String>
        get() = _query
    val state: MutableStateFlow<RefreshableUiState<List<LastFmStatsEntity>>> =
        MutableStateFlow(RefreshableUiState.Success(null, true))

    fun updateEntry(new: String) {
        _query.updateValue(new)
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            query.flatMapLatest { key ->
                repo.artistStore.stream(StoreRequest.fresh(key))
            }.collect {
                state.update(it)
            }
            /*query
                .debounce(300)
                .filter {input ->
                    input.isNotEmpty()
                }
                .distinctUntilChanged()
                .flatMapLatest {key ->
                    repo.artistStore
                        .stream(StoreRequest.fresh(key))
                        .combine(repo.albumStore.stream(StoreRequest.fresh(key))) {artists, albums ->
                            artists to albums
                        }
                }
                .collect {
                    state.update(it.first)
                    albumState.update(it.second)
                }*/
        }
    }
}