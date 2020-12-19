package de.schnettler.scrobbler.ui.history

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import de.schnettler.database.models.Scrobble
import de.schnettler.repo.LocalRepository
import de.schnettler.repo.ScrobbleRepository
import de.schnettler.repo.mapping.response.LastFmResponse
import de.schnettler.scrobbler.ui.common.compose.RefreshableStateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocalViewModel @ViewModelInject constructor(
    repo: LocalRepository,
    private val scrobbleRepo: ScrobbleRepository
) : RefreshableStateViewModel<String, List<Scrobble>, List<Scrobble>>(store = repo.recentTracksStore, "") {

    val cachedScrobblesCOunt by lazy {
        repo.getNumberOfCachedScrobbles()
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

    fun editCachedScrobble(scrobble: Scrobble) {
        viewModelScope.launch(Dispatchers.IO) {
            scrobbleRepo.submitScrobbleEdit(scrobble)
        }
    }
}