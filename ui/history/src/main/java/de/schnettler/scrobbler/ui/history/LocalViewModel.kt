package de.schnettler.scrobbler.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.database.models.Scrobble
import de.schnettler.repo.LocalRepository
import de.schnettler.repo.ScrobbleRepository
import de.schnettler.repo.mapping.response.LastFmResponse
import de.schnettler.scrobbler.ui.common.compose.RefreshableStateViewModel
import de.schnettler.scrobbler.ui.history.mapper.RejectionCodeToReasonMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalViewModel @Inject constructor(
    private val repo: LocalRepository,
    private val scrobbleRepo: ScrobbleRepository,
    private val rejectionCodeToReasonMapper: RejectionCodeToReasonMapper
) : RefreshableStateViewModel<String, List<Scrobble>, List<Scrobble>>(store = repo.recentTracksStore, "") {

    val isSubmitting = MutableStateFlow(false)

    val events = MutableLiveData<Event<SubmissionEvent>>()

    val cachedScrobblesCOunt by lazy {
        repo.getNumberOfCachedScrobbles()
    }

    fun scheduleScrobbleSubmission() {
        isSubmitting.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val result = scrobbleRepo.submitCachedScrobbles()

            val errorMessage = result.errors.firstOrNull()?.description ?: result.exceptions.firstOrNull()?.message
            val mappedResult = SubmissionResult(
                accepted = result.accepted.map { it.timestamp },
                ignored = result.ignored.associateBy({ it.timestamp }, { it.ignoredMessage.code }),
                error = errorMessage
            )
            events.postValue(Event(SubmissionEvent.Success(mappedResult)))
            isSubmitting.value = false
        }
    }

    fun showDetails(submissionResult: SubmissionResult) {
        viewModelScope.launch {
            val accepted = async { repo.getScrobblesById(submissionResult.accepted) }
            val ignored = async { repo.getScrobblesById(submissionResult.ignored.keys.toList()) }
            val ignoredWithReason = ignored.await().associateWith { scrobble ->
                rejectionCodeToReasonMapper.map(
                    submissionResult.ignored.getOrElse(scrobble.timestamp) { 0L }
                )
            }
            events.postValue(
                Event(
                    SubmissionEvent.ShowDetails(
                        accepted.await(),
                        ignoredWithReason,
                        submissionResult.error
                    )
                )
            )
        }
    }

    fun submitScrobble(track: Scrobble) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = scrobbleRepo.submitScrobble(track)
            if (response is LastFmResponse.SUCCESS) {
                scrobbleRepo.markScrobblesAsSubmitted(track)
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