package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.schnettler.database.models.TopListEntryType
import de.schnettler.repo.Repository

class ChartsViewModel(val repo: Repository) : ViewModel() {
    val artistResponse by lazy {
        repo.getArtistChart(TopListEntryType.CHART_ARTIST).asLiveData(viewModelScope.coroutineContext)
    }
}