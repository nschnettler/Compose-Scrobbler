package de.schnettler.scrobbler.ui.profile

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.repo.TopListRepository
import de.schnettler.repo.UserRepository
import de.schnettler.repo.preferences.PreferenceEntry
import de.schnettler.scrobbler.ui.common.compose.model.MediaCardSize
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
    dataStoreManager: DataStoreManager
) : ProfileViewModel() {
    override val timePeriod = MutableStateFlow(UITimePeriod.OVERALL)
    override val showFilterDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val mediaCardSize = dataStoreManager.getPreferenceFlow(PreferenceEntry.MediaCardSize).map {
        try {
            MediaCardSize.valueOf(it)
        } catch (e: IllegalArgumentException) {
            MediaCardSize.MEDIUM
        }.size
    }

    override fun updatePeriod(period: UITimePeriod) = timePeriod.updateValue(period)
    override fun showDialog(show: Boolean) = showFilterDialog.updateValue(show)

    init {
        userRepo.userStore.streamIntoState("") { state, user ->
            state?.copy(user = user) ?: ProfileViewState(user = user)
        }

        viewModelScope.launch {
            timePeriod.collectLatest {
                topListRepo.topArtistStore.streamIntoState(it.period) { state, toplist ->
                    state?.copy(topArtists = toplist) ?: ProfileViewState(topArtists = toplist)
                }
                topListRepo.topAlbumStore.streamIntoState(it.period) { state, toplist ->
                    state?.copy(topAlbums = toplist) ?: ProfileViewState(topAlbums = toplist)
                }
                topListRepo.topTracksStore.streamIntoState(it.period) { state, toplist ->
                    state?.copy(topTracks = toplist) ?: ProfileViewState(topTracks = toplist)
                }
            }
        }
    }

    override fun refresh() {
        userRepo.userStore.refreshIntoState("")
        timePeriod.value.period.also {
            topListRepo.topAlbumStore.refreshIntoState(it)
            topListRepo.topTracksStore.refreshIntoState(it)
            topListRepo.topArtistStore.refreshIntoState(it)
        }
    }
}