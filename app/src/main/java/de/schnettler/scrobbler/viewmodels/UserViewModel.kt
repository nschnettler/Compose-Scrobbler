package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.ListingMin
import de.schnettler.database.models.TopListEntryType
import de.schnettler.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserViewModel(private val repo: Repository) : ViewModel() {
    val albumState = MutableStateFlow(LoadingState<List<ListingMin>>(listOf()))
    val artistState = MutableStateFlow(LoadingState<List<ListingMin>>(listOf()))
    val trackState = MutableStateFlow(LoadingState<List<ListingMin>>(listOf()))

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
            artistResponse.collect {
                artistState.value.updateState(artistState, it)
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            aalbumResponse.collect {
                albumState.value.updateState(albumState, it)
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            trackResponse.collect {
                trackState.value.updateState(trackState, it)
            }
        }
    }
}



data class LoadingState<T>(
    val data: T,
    val loading: Boolean = true,
    val error: String = ""
) {
    fun updateState(flow: MutableStateFlow<LoadingState<T>>, response: StoreResponse<T>) {
        when(response) {
            is StoreResponse.Data -> {
                flow.value = flow.value.copy(data = response.value, loading = false, error = "")
            }
            is StoreResponse.Loading -> {
                flow.value = flow.value.copy(loading = true, error = "")
            }
            is StoreResponse.Error -> {
                flow.value = flow.value.copy(loading = false, error = response.errorMessageOrNull() ?: "undef")
            }
        }
    }
}