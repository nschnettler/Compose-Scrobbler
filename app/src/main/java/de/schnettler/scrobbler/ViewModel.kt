package de.schnettler.scrobbler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import de.schnettler.repo.Repository

class MainViewModel: ViewModel() {
    private val repo = Repository()

    val topArtists by lazy {
        repo.getTopArtists().asLiveData()
    }
}