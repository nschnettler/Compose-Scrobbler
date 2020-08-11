package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.database.models.Scrobble
import de.schnettler.repo.LocalRepository
import de.schnettler.repo.Result
import de.schnettler.repo.ScrobbleRepository
import de.schnettler.repo.mapping.LastFmResponse
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.scrobbler.util.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LocalViewModel @ViewModelInject constructor(
    private val repo: LocalRepository,
    private val scrobbleRepo: ScrobbleRepository
) : ViewModel() {

    val recentTracksState: MutableStateFlow<RefreshableUiState<List<Scrobble>>> =
            MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))

    val cachedScrobblesCOunt by lazy {
        repo.getNumberOfCachedScrobbles()
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getRecentTracks().collect {
                recentTracksState.update(it)
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun refresh() {
        recentTracksState.update(Result.Loading)
        viewModelScope.launch {
            try {
                repo.refreshRecentTracks()
            } catch (e: Exception) {
                recentTracksState.update(Result.Error(e))
            }
        }
    }

    fun scheduleScrobbleSubmission() = scrobbleRepo.scheduleScrobble()

    fun submitScrobble(track: Scrobble) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = scrobbleRepo.submitScrobble(track)
            if (response is LastFmResponse.SUCCESS) {
                scrobbleRepo.markScrobblesAsSubmitted(listOf(track))
            }
        }
    }

    fun deleteScrobble(scrobble: Scrobble) {
        viewModelScope.launch(Dispatchers.IO) {
            scrobbleRepo.deleteScrobble(scrobble)
        }
    }
}