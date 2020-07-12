package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.database.models.LocalTrack
import de.schnettler.repo.LocalRepository
import de.schnettler.repo.mapping.LastFmPostResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class LocalViewModel @ViewModelInject constructor(
    private val repo: LocalRepository
) : ViewModel() {
    val data by lazy {
        repo.getData()
    }

    fun submitScrobble(track: LocalTrack) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.createAndSubmitScrobble(track)
            handleResult(result)
        }
    }

    fun submitNowPlaying(track: LocalTrack) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.submitNowPlaying(track)
            handleResult(result)
        }
    }

    private fun handleResult(result: LastFmPostResponse) {
        when(result) {
            is LastFmPostResponse.ERROR -> Timber.e("${result.error}")
            is LastFmPostResponse.SUCCESS<*> -> Timber.d("${result.data}")
        }
    }
}