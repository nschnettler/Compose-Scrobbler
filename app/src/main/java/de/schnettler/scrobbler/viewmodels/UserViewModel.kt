package de.schnettler.scrobbler.viewmodels

import androidx.compose.MutableState
import androidx.lifecycle.*
import de.schnettler.database.models.*
import de.schnettler.repo.Repository
import de.schnettler.scrobbler.model.LoadingState
import de.schnettler.scrobbler.model.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

class UserViewModel(private val repo: Repository) : ViewModel() {
    val albumState: MutableStateFlow<LoadingState<List<ListingMin>>?> = MutableStateFlow(null)
    val artistState: MutableStateFlow<LoadingState<List<ListingMin>>?> = MutableStateFlow(null)
    val trackState: MutableStateFlow<LoadingState<List<ListingMin>>?> = MutableStateFlow(null)
    val userState: MutableStateFlow<LoadingState<User>?> = MutableStateFlow(null)
    val lovedTracksState: MutableStateFlow<LoadingState<List<Track>>?> = MutableStateFlow(null)

    val userInfo by lazy { repo.getUserInfo() }

    private val artistResponse by lazy {
        repo.getTopList(TopListEntryType.USER_ARTIST)
    }

    private val trackResponse by lazy {
        repo.getTopList(TopListEntryType.USER_TRACKS)
    }

    private val aalbumResponse by lazy {
        repo.getTopList(TopListEntryType.USER_ALBUM)
    }

    private val lovedTracksResponse by lazy {
        repo.getUserLovedTracks()
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userInfo.collect { userState.update(it) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            artistResponse.collect { artistState.update(it) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            aalbumResponse.collect { albumState.update(it) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            trackResponse.collect { trackState.update(it) }
        }
        viewModelScope.launch {
            lovedTracksResponse.collect { lovedTracksState.update(it) }
        }
    }
}