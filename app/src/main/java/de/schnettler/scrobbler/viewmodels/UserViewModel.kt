package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.common.TimePeriod
import de.schnettler.database.models.TopListEntryWithData
import de.schnettler.database.models.Track
import de.schnettler.database.models.User
import de.schnettler.repo.TopListRepository
import de.schnettler.repo.UserRepository
import de.schnettler.scrobbler.model.LoadingState
import de.schnettler.scrobbler.model.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class UserViewModel @ViewModelInject constructor(
    private val topListRepo: TopListRepository,
    private val userRepo: UserRepository
): ViewModel() {
    private var _timePeriod: MutableStateFlow<TimePeriod> = MutableStateFlow(TimePeriod.OVERALL)
    val timePeriod: StateFlow<TimePeriod>
        get() = _timePeriod

    val albumState: MutableStateFlow<LoadingState<List<TopListEntryWithData>>?> = MutableStateFlow(null)
    val artistState: MutableStateFlow<LoadingState<List<TopListEntryWithData>>?> = MutableStateFlow(null)
    val trackState: MutableStateFlow<LoadingState<List<TopListEntryWithData>>?> = MutableStateFlow(null)
    val userState: MutableStateFlow<LoadingState<User>?> = MutableStateFlow(null)
    private val lovedTracksState: MutableStateFlow<LoadingState<List<Track>>?> = MutableStateFlow(null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Loading Artists")
            timePeriod.flatMapLatest { topListRepo.getTopArtists(it) }.collect { artistState.update(it) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Loading Albums")
            timePeriod.flatMapLatest {topListRepo.getTopAlbums(it) }.collect { albumState.update(it) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            timePeriod.flatMapLatest {topListRepo.getTopTracks(it) }.collect { trackState.update(it) }
        }
        viewModelScope.launch {
            userRepo.getUserLovedTracks().collect { lovedTracksState.update(it) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            userRepo.getUserInfo().collect { userState.update(it) }
        }
    }

    fun updatePeriod(newPeriod: TimePeriod) {
        if (newPeriod != timePeriod.value) {
            _timePeriod.value = newPeriod
        }
    }
}