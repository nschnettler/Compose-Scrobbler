package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.Artist
import de.schnettler.repo.SearchRepository
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
    val state: MutableStateFlow<StoreResponse<List<Artist>>> = MutableStateFlow(StoreResponse.Loading(origin = ResponseOrigin.Fetcher))

    fun updateEntry(new: String) {
        _query.updateValue(new)
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            query.flatMapLatest { key ->
                repo.artistStore.stream(StoreRequest.fresh(key))
            }.collect {
                state.value = it
            }
        }
    }
}