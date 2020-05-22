package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.schnettler.database.models.ListingMin
import de.schnettler.database.models.TopListEntryType
import de.schnettler.repo.Repository
import de.schnettler.scrobbler.model.LoadingState
import de.schnettler.scrobbler.model.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserViewModel(private val repo: Repository) : ViewModel() {
    val albumState: MutableStateFlow<LoadingState<List<ListingMin>>?> = MutableStateFlow(null)
    val artistState: MutableStateFlow<LoadingState<List<ListingMin>>?> = MutableStateFlow(null)
    val trackState: MutableStateFlow<LoadingState<List<ListingMin>>?> = MutableStateFlow(null)

    val userInfo by lazy {
        repo.getUserInfo().asLiveData(viewModelScope.coroutineContext)
    }

    private val artistResponse by lazy {
        repo.getTopList(TopListEntryType.USER_ARTIST)
    }

    private val trackResponse by lazy {
        repo.getTopList(TopListEntryType.USER_TRACKS)
    }

    private val aalbumResponse by lazy {
        repo.getTopList(TopListEntryType.USER_ALBUM)
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            artistResponse.collect { artistState.update(it) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            aalbumResponse.collect { albumState.update(it) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            trackResponse.collect { trackState.update(it) }
        }
    }
}