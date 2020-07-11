package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import de.schnettler.database.models.LocalTrack
import de.schnettler.repo.LocalRepository

class LocalViewModel @ViewModelInject constructor(
    private val repo: LocalRepository
) : ViewModel() {
    val data by lazy {
        repo.getData()
    }

    fun submitScrobble(track: LocalTrack) {
        repo.requestScrobble(track)
    }
}