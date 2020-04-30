package de.schnettler.scrobbler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import de.schnettler.repo.Repository

class MainViewModel: ViewModel() {
    val repo = Repository()

    val topArtists = repo.getTopArtists().asLiveData()
}