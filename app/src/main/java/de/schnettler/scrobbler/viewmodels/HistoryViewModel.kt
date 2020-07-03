package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.Track
import de.schnettler.repo.UserRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class HistoryViewModel @ViewModelInject constructor(private val repo: UserRepository) : ViewModel() {
    val recentTracks = MutableLiveData<List<Track>>()
    val isRefreshing = MutableLiveData<Boolean>()

    fun refreshHistory() {
        Timber.d("Refreshing History")
        viewModelScope.launch {
            isRefreshing.value = true
            recentTracks.value = repo.getUserRecentTrack()
            isRefreshing.value = false
        }
    }

    init {
        refreshHistory()
    }
}