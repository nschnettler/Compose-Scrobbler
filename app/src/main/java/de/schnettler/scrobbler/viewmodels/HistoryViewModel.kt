package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.database.models.Track
import de.schnettler.repo.UserRepository
import de.schnettler.scrobbler.util.Result
import kotlinx.coroutines.launch
import timber.log.Timber

class HistoryViewModel @ViewModelInject constructor(private val repo: UserRepository) : ViewModel() {
    fun refreshHistory(callback: (Result<List<Track>>) -> Unit) {
        Timber.d("Refreshing History")
        viewModelScope.launch {
            try {
                val response = repo.getUserRecentTrack()
                callback(Result.Success(response))
            } catch (e: Exception) {
                callback(Result.Error(e))
            }
        }
    }
}