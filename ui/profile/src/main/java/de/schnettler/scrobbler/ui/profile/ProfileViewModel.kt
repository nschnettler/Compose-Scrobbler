package de.schnettler.scrobbler.ui.profile

import androidx.compose.ui.unit.Dp
import de.schnettler.scrobbler.ui.common.compose.viewmodel.StoreViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

abstract class ProfileViewModel : StoreViewModel<ProfileViewState>() {
    abstract val timePeriod: StateFlow<UITimePeriod>
    abstract val showFilterDialog: StateFlow<Boolean>

    abstract fun refresh()

    abstract fun updatePeriod(period: UITimePeriod): Boolean

    abstract fun showDialog(show: Boolean): Boolean
    abstract val mediaCardSize: Flow<Dp>
}