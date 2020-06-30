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
import de.schnettler.scrobbler.model.update2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class UserViewModel @ViewModelInject constructor(
    private val topListRepo: TopListRepository,
    private val userRepo: UserRepository
) : ViewModel() {
    private var _timePeriod: MutableStateFlow<TimePeriod> = MutableStateFlow(TimePeriod.OVERALL)
    val timePeriod: StateFlow<TimePeriod>
        get() = _timePeriod

    private val _albumState: MutableStateFlow<LoadingState<List<TopListEntryWithData>>> =
        MutableStateFlow(LoadingState.Initial())
    val albumState: StateFlow<LoadingState<List<TopListEntryWithData>>>
        get() = _albumState

    private val _artistState: MutableStateFlow<LoadingState<List<TopListEntryWithData>>> =
        MutableStateFlow(LoadingState.Initial())
    val artistState: StateFlow<LoadingState<List<TopListEntryWithData>>>
        get() = _artistState

    private val _trackState: MutableStateFlow<LoadingState<List<TopListEntryWithData>>> =
        MutableStateFlow(LoadingState.Initial())
    val trackState: StateFlow<LoadingState<List<TopListEntryWithData>>>
        get() = _trackState

    private val _userState: MutableStateFlow<LoadingState<User>> =
        MutableStateFlow(LoadingState.Initial())
    val userState: StateFlow<LoadingState<User>>
        get() = _userState

    private val _lovedTracksState: MutableStateFlow<LoadingState<List<Track>>> =
        MutableStateFlow(LoadingState.Initial())
    val lovedTracksState: StateFlow<LoadingState<List<Track>>>
        get() = _lovedTracksState

    private val _showFilterDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showFilterDialog: StateFlow<Boolean>
        get() = _showFilterDialog

    init {
        viewModelScope.launch(Dispatchers.IO) {
            timePeriod.flatMapLatest { topListRepo.getTopArtists(it) }
                .collect { _artistState.update2(it) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            timePeriod.flatMapLatest { topListRepo.getTopAlbums(it) }
                .collect { _albumState.update2(it) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            timePeriod.flatMapLatest { topListRepo.getTopTracks(it) }
                .collect { _trackState.update2(it) }
        }
        viewModelScope.launch {
            userRepo.getUserLovedTracks().collect { _lovedTracksState.update2(it) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            userRepo.getUserInfo().collect { _userState.update2(it) }
        }
    }

    fun updatePeriod(period: TimePeriod) = _timePeriod.updateValue(period)

    fun showDialog(show: Boolean) = _showFilterDialog.updateValue(show)
}

@ExperimentalCoroutinesApi
fun <T> MutableStateFlow<T>.updateValue(newValue: T) {
    if (value != newValue) {
        value = newValue
    }
}