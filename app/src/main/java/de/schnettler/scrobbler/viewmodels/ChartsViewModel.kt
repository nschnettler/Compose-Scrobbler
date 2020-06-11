package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.schnettler.repo.ChartRepository

class ChartsViewModel @ViewModelInject constructor(
    private val repo: ChartRepository
) : ViewModel() {
    val artistResponse by lazy {
        repo.getArtistChart().asLiveData(viewModelScope.coroutineContext)
    }
}