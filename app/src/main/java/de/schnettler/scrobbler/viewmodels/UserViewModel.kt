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
    private val lovedTracksState: MutableStateFlow<LoadingState<List<Track>>?> = MutableStateFlow(null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getTopList(TopListEntryType.USER_ARTIST, timePeriod).collect { artistState.update(it) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repo.getTopList(TopListEntryType.USER_ALBUM, timePeriod).collect { albumState.update(it) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repo.getTopList(TopListEntryType.USER_TRACKS, timePeriod).collect { trackState.update(it) }
        }
        viewModelScope.launch {
            repo.getUserLovedTracks().collect { lovedTracksState.update(it) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repo.getUserInfo().collect { userState.update(it) }
        }
    }
}