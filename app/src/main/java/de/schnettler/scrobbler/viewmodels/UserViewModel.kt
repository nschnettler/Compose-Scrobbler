package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.fresh
import de.schnettler.common.TimePeriod
import de.schnettler.database.models.TopListAlbum
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListTrack
import de.schnettler.database.models.User
import de.schnettler.repo.Result
import de.schnettler.repo.TopListRepository
import de.schnettler.repo.UserRepository
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.scrobbler.util.update
import de.schnettler.scrobbler.util.updateError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import timber.log.Timber

class UserViewModel @ViewModelInject constructor(
    private val topListRepo: TopListRepository,
    private val userRepo: UserRepository
) : ViewModel() {
    val uiState: MutableStateFlow<RefreshableUiState<ProfileScreenState>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))

    private var _timePeriod: MutableStateFlow<TimePeriod> = MutableStateFlow(TimePeriod.OVERALL)
    val timePeriod: StateFlow<TimePeriod>
        get() = _timePeriod

    private val _showFilterDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showFilterDialog: StateFlow<Boolean>
        get() = _showFilterDialog

    init {
        Timber.d("Init")
        viewModelScope.launch(Dispatchers.IO) {
            userRepo.userStore.stream(StoreRequest.cached("", true))
                .filter { it !is StoreResponse.Loading }.collectLatest {
                    when (it) {
                        is StoreResponse.Data -> {
                            val newData = uiState.value.currentData?.copy(user = it.value)
                                ?: ProfileScreenState(user = it.value)
                            uiState.update(Result.Success(newData))
                        }
                        is StoreResponse.Error -> {
                            uiState.updateError(it)
                        }
                        else -> {
                        }
                    }
                }
        }
        viewModelScope.launch(Dispatchers.IO) {
            timePeriod.collectLatest { time ->
                uiState.update(Result.Loading)

                launch {
                    topListRepo.topArtistStore.stream(StoreRequest.cached(time, true))
                        .collectLatest {
                            when (it) {
                                is StoreResponse.Data -> {
                                    val newData =
                                        uiState.value.currentData?.copy(artists = it.value)
                                            ?: ProfileScreenState(artists = it.value)
                                    Timber.d("New Data: $newData")
                                    uiState.update(Result.Success(newData))
                                }
                                is StoreResponse.Error -> {
                                    uiState.updateError(it)
                                }
                                else -> {
                                }
                            }
                        }
                }

                launch {
                    topListRepo.topAlbumStore.stream(StoreRequest.cached(time, true))
                        .collectLatest {
                            when (it) {
                                is StoreResponse.Data -> {
                                    val newData = uiState.value.currentData?.copy(albums = it.value)
                                        ?: ProfileScreenState(albums = it.value)
                                    uiState.update(Result.Success(newData))
                                }
                                is StoreResponse.Error -> {
                                    uiState.updateError(it)
                                }
                                else -> {
                                }
                            }
                        }
                }

                launch {
                    topListRepo.topTracksStore.stream(StoreRequest.cached(time, true))
                        .collectLatest {
                            when (it) {
                                is StoreResponse.Data -> {
                                    val newData = uiState.value.currentData?.copy(tracks = it.value)
                                        ?: ProfileScreenState(tracks = it.value)
                                    uiState.update(Result.Success(newData))
                                }
                                is StoreResponse.Error -> {
                                    uiState.updateError(it)
                                }
                                else -> {
                                }
                            }
                        }
                }
            }
        }
    }

    fun updatePeriod(period: TimePeriod) = _timePeriod.updateValue(period)

    fun showDialog(show: Boolean) = _showFilterDialog.updateValue(show)

    fun refresh() {
        viewModelScope.launch {
            uiState.update(Result.Loading)
            try {
                userRepo.userStore.fresh("")
            } catch (e: Exception) {
                uiState.update(Result.Error(e))
            }
        }
    }
}

fun <T> MutableStateFlow<T>.updateValue(newValue: T): Boolean {
    if (value != newValue) {
        value = newValue
        return true
    }
    return false
}

data class ProfileScreenState(
    val user: User? = null,
    val artists: List<TopListArtist> = listOf(),
    val albums: List<TopListAlbum> = listOf(),
    val tracks: List<TopListTrack> = listOf()
)