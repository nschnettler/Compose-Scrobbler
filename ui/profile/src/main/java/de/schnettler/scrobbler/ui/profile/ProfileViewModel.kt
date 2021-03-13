package de.schnettler.scrobbler.ui.profile

import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import de.schnettler.database.models.TopListAlbum
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListTrack
import de.schnettler.database.models.User
import de.schnettler.scrobbler.ui.common.compose.RefreshableUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

abstract class ProfileViewModel : ViewModel() {
    abstract val timePeriod: StateFlow<UITimePeriod>
    abstract val albumState: StateFlow<RefreshableUiState<List<TopListAlbum>>>
    abstract val artistState: StateFlow<RefreshableUiState<List<TopListArtist>>>
    abstract val trackState: StateFlow<RefreshableUiState<List<TopListTrack>>>
    abstract val userState: StateFlow<RefreshableUiState<User>>
    abstract val showFilterDialog: StateFlow<Boolean>

    abstract fun refresh()

    abstract fun updatePeriod(period: UITimePeriod): Boolean

    abstract fun showDialog(show: Boolean): Boolean
    abstract val mediaCardSize: Flow<Dp>
}