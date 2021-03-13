package de.schnettler.scrobbler.ui.profile

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.database.models.TopListAlbum
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListTrack
import de.schnettler.database.models.User
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.repo.TopListRepository
import de.schnettler.repo.UserRepository
import de.schnettler.repo.preferences.PreferenceEntry
import de.schnettler.scrobbler.ui.common.compose.RefreshableUiState
import de.schnettler.scrobbler.ui.common.compose.freshFrom
import de.schnettler.scrobbler.ui.common.compose.model.MediaCardSize
import de.schnettler.scrobbler.ui.common.compose.refreshStateFlowFromStore
import de.schnettler.scrobbler.ui.common.compose.streamFrom
import de.schnettler.scrobbler.ui.common.compose.updateValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModelImpl @Inject constructor(
    private val topListRepo: TopListRepository,
    private val userRepo: UserRepository,
    private val dataStoreManager: DataStoreManager
) : ProfileViewModel() {
    override val timePeriod = MutableStateFlow(UITimePeriod.OVERALL)

    override val albumState: MutableStateFlow<RefreshableUiState<List<TopListAlbum>>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))

    override val artistState: MutableStateFlow<RefreshableUiState<List<TopListArtist>>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))

    override val trackState: MutableStateFlow<RefreshableUiState<List<TopListTrack>>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))

    override val userState: MutableStateFlow<RefreshableUiState<User>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))

    override val showFilterDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val mediaCardSize = dataStoreManager.getPreferenceFlow(PreferenceEntry.MediaCardSize).map {
        try {
            MediaCardSize.valueOf(it)
        } catch (e: IllegalArgumentException) {
            MediaCardSize.MEDIUM
        }.size
    }

    init {
        viewModelScope.launch {
            launch { userState.streamFrom(userRepo.userStore, "") }
            launch { refreshStateFlowFromStore(null, userRepo.lovedTracksStore, "") }
            timePeriod.collectLatest { uiTime ->
                launch { artistState.streamFrom(topListRepo.topArtistStore, uiTime.period) }
                launch { albumState.streamFrom(topListRepo.topAlbumStore, uiTime.period) }
                launch { trackState.streamFrom(topListRepo.topTracksStore, uiTime.period) }
            }
        }
    }

    override fun refresh() {
        viewModelScope.apply {
            launch { artistState.freshFrom(topListRepo.topArtistStore, timePeriod.value.period) }
            launch { albumState.freshFrom(topListRepo.topAlbumStore, timePeriod.value.period) }
            launch { trackState.freshFrom(topListRepo.topTracksStore, timePeriod.value.period) }
            launch { userState.freshFrom(userRepo.userStore, "") }
            launch { refreshStateFlowFromStore(null, userRepo.lovedTracksStore, "") }
        }
    }

    override fun updatePeriod(period: UITimePeriod) = timePeriod.updateValue(period)

    override fun showDialog(show: Boolean) = showFilterDialog.updateValue(show)
}