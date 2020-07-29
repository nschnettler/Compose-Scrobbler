package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.fresh
import de.schnettler.common.TimePeriod
import de.schnettler.database.models.TopListEntryWithData
import de.schnettler.database.models.User
import de.schnettler.repo.Result
import de.schnettler.repo.TopListRepository
import de.schnettler.repo.UserRepository
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.scrobbler.util.update
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

    private val _albumState: MutableStateFlow<RefreshableUiState<List<TopListEntryWithData>>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))
    val albumState: StateFlow<RefreshableUiState<List<TopListEntryWithData>>>
        get() = _albumState

    private val _artistState: MutableStateFlow<RefreshableUiState<List<TopListEntryWithData>>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))
    val artistState: StateFlow<RefreshableUiState<List<TopListEntryWithData>>>
        get() = _artistState

    private val _trackState: MutableStateFlow<RefreshableUiState<List<TopListEntryWithData>>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))
    val trackState: StateFlow<RefreshableUiState<List<TopListEntryWithData>>>
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
            launch {
                userRepo.userStore.stream(StoreRequest.cached("", true)).collectLatest {
                    _userState.update(it)
                }
            }
            launch { userRepo.lovedTracksStore.stream(StoreRequest.fresh("")) }
            timePeriod.collectLatest { period ->
                launch {
                    topListRepo.topArtistStore.stream(StoreRequest.cached(period, true)).collectLatest {
                        _artistState.update(it)
                    }
                }
                launch {
                    topListRepo.topAlbumStore.stream(StoreRequest.cached(period, true)).collectLatest {
                        _albumState.update(it)
                    }
                }
                launch {
                    topListRepo.topTracksStore.stream(StoreRequest.cached(period, true)).collectLatest {
                        _trackState.update(it)
                    }
                }
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun refresh() {
        viewModelScope.apply {
            launch {
                _artistState.update(Result.Loading)
                try {
                    topListRepo.topArtistStore.fresh(timePeriod.value)
                } catch (e: Exception) {
                    _artistState.update(Result.Error(e))
                }
            }
            launch {
                _albumState.update(Result.Loading)
                try {
                    topListRepo.topAlbumStore.fresh(timePeriod.value)
                } catch (e: Exception) {
                    _albumState.update(Result.Error(e))
                }
            }
            launch {
                _trackState.update(Result.Loading)
                try {
                    topListRepo.topTracksStore.fresh(timePeriod.value)
                } catch (e: Exception) {
                    _trackState.update(Result.Error(e))
                }
            }
            launch {
                _userState.update(Result.Loading)
                try {
                    userRepo.userStore.fresh("")
                    userRepo.lovedTracksStore.fresh("")
                } catch (e: Exception) {
                    _userState.update(Result.Error(e))
                }
            }
        }
    }

    fun updatePeriod(period: TimePeriod) = _timePeriod.updateValue(period)

    fun showDialog(show: Boolean) = _showFilterDialog.updateValue(show)
}

fun <T> MutableStateFlow<T>.updateValue(newValue: T): Boolean {
    if (value != newValue) {
        value = newValue
        return true
    }
    return false
}