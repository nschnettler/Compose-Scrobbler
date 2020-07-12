package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.database.models.LocalTrack
import de.schnettler.repo.LocalRepository
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
            val test = repo.createAndSubmitScrobble(track)
            if (test.isSuccessful) {
                Timber.d("Result ${test.body()}")
            } else {
                Timber.e("Error ${test.errorBody()}")
            }
            Timber.d("Result: $test")
        }
    }
}