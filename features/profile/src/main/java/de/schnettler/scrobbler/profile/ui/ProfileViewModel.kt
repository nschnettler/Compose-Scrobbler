package de.schnettler.scrobbler.profile.ui

import androidx.compose.ui.unit.Dp
import de.schnettler.scrobbler.core.ui.viewmodel.StoreViewModel
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