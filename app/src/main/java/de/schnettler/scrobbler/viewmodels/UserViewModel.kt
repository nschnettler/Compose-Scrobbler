package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.common.TimePeriod
import de.schnettler.database.models.TopListAlbum
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListTrack
import de.schnettler.database.models.User
import de.schnettler.repo.TopListRepository
import de.schnettler.repo.UserRepository
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.scrobbler.util.freshFrom
import de.schnettler.scrobbler.util.refreshStateFlowFromStore
import de.schnettler.scrobbler.util.streamFrom
import de.schnettler.scrobbler.util.updateValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserViewModel @ViewModelInject constructor(
    private val topListRepo: TopListRepository,
    private val userRepo: UserRepository
) : ViewModel() {
    private var _timePeriod: MutableStateFlow<TimePeriod> = MutableStateFlow(TimePeriod.OVERALL)
    val timePeriod: StateFlow<TimePeriod>
        get() = _timePeriod

    private val _albumState: MutableStateFlow<RefreshableUiState<List<TopListAlbum>>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))
    val albumState: StateFlow<RefreshableUiState<List<TopListAlbum>>>
        get() = _albumState

    private val _artistState: MutableStateFlow<RefreshableUiState<List<TopListArtist>>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))
    val artistState: StateFlow<RefreshableUiState<List<TopListArtist>>>
        get() = _artistState

    private val _trackState: MutableStateFlow<RefreshableUiState<List<TopListTrack>>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))
    val trackState: StateFlow<RefreshableUiState<List<TopListTrack>>>
        get() = _trackState

    private val _userState: MutableStateFlow<RefreshableUiState<User>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))
    val userState: StateFlow<RefreshableUiState<User>>
        get() = _userState

    private val _showFilterDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showFilterDialog: StateFlow<Boolean>
        get() = _showFilterDialog

    init {
        viewModelScope.launch {
            launch { _userState.streamFrom(userRepo.userStore, "") }
            launch { refreshStateFlowFromStore(null, userRepo.lovedTracksStore, "") }
            timePeriod.collectLatest { period ->
                launch { _artistState.streamFrom(topListRepo.topArtistStore, period) }
                launch { _albumState.streamFrom(topListRepo.topAlbumStore, period) }
                launch { _trackState.streamFrom(topListRepo.topTracksStore, period) }
            }
        }
    }

    fun refresh() {
        viewModelScope.apply {
            launch { _artistState.freshFrom(topListRepo.topArtistStore, timePeriod.value) }
            launch { _albumState.freshFrom(topListRepo.topAlbumStore, timePeriod.value) }
            launch { _trackState.freshFrom(topListRepo.topTracksStore, timePeriod.value) }
            launch { _userState.freshFrom(userRepo.userStore, "") }
            launch { refreshStateFlowFromStore(null, userRepo.lovedTracksStore, "") }
        }
    }

    fun updatePeriod(period: TimePeriod) = _timePeriod.updateValue(period)

    fun showDialog(show: Boolean) = _showFilterDialog.updateValue(show)
}