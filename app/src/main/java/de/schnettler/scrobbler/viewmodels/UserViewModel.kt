package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.common.TimePeriod
import de.schnettler.database.models.*
import de.schnettler.repo.Repository
import de.schnettler.scrobbler.model.LoadingState
import de.schnettler.scrobbler.model.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class UserViewModel(private val repo: Repository) : ViewModel() {
    private var _timePeriod: MutableStateFlow<TimePeriod> = MutableStateFlow(TimePeriod.OVERALL)
    val timePeriod: StateFlow<TimePeriod>
        get() = _timePeriod

    val albumState: MutableStateFlow<LoadingState<List<ListingMin>>?> = MutableStateFlow(null)
    val artistState: MutableStateFlow<LoadingState<List<ListingMin>>?> = MutableStateFlow(null)
    val trackState: MutableStateFlow<LoadingState<List<ListingMin>>?> = MutableStateFlow(null)
    val userState: MutableStateFlow<LoadingState<User>?> = MutableStateFlow(null)
    private val lovedTracksState: MutableStateFlow<LoadingState<List<Track>>?> = MutableStateFlow(null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Loading Artists")
            timePeriod.flatMapLatest { repo.getTopArtists(it) }.collect { artistState.update(it) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Loading Albums")
            timePeriod.flatMapLatest {repo.getTopList(TopListEntryType.USER_ALBUM, it) }.collect { albumState.update(it) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            timePeriod.flatMapLatest {repo.getTopList(TopListEntryType.USER_TRACKS, it) }.collect { trackState.update(it) }
        }
        viewModelScope.launch {
            repo.getUserLovedTracks().collect { lovedTracksState.update(it) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repo.getUserInfo().collect { userState.update(it) }
        }
    }

    fun updatePeriod(newPeriod: TimePeriod) {
        if (newPeriod != timePeriod.value) {
            _timePeriod.value = newPeriod
        }
    }
}