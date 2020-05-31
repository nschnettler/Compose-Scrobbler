package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.common.TimePeriod
import de.schnettler.database.models.ListingMin
import de.schnettler.database.models.TopListEntryType
import de.schnettler.database.models.Track
import de.schnettler.database.models.User
import de.schnettler.repo.Repository
import de.schnettler.scrobbler.model.LoadingState
import de.schnettler.scrobbler.model.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserViewModel(private val repo: Repository) : ViewModel() {
    private val timePeriod = TimePeriod.MONTH
    val albumState: MutableStateFlow<LoadingState<List<ListingMin>>?> = MutableStateFlow(null)
    val artistState: MutableStateFlow<LoadingState<List<ListingMin>>?> = MutableStateFlow(null)
    val trackState: MutableStateFlow<LoadingState<List<ListingMin>>?> = MutableStateFlow(null)
    val userState: MutableStateFlow<LoadingState<User>?> = MutableStateFlow(null)
    val lovedTracksState: MutableStateFlow<LoadingState<List<Track>>?> = MutableStateFlow(null)

    val userInfo by lazy { repo.getUserInfo() }

    private val artistResponse by lazy {
        repo.getTopList(TopListEntryType.USER_ARTIST, timePeriod)
    }

    private val trackResponse by lazy {
        repo.getTopList(TopListEntryType.USER_TRACKS, timePeriod)
    }

    private val aalbumResponse by lazy {
        repo.getTopList(TopListEntryType.USER_ALBUM, timePeriod)
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