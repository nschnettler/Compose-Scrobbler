package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.database.models.Artist
import de.schnettler.database.models.ListingMin
import de.schnettler.database.models.Track
import de.schnettler.repo.DetailRepository
import de.schnettler.scrobbler.model.LoadingState
import de.schnettler.scrobbler.model.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class DetailViewModel @ViewModelInject constructor(repo: DetailRepository)  : ViewModel() {
    private val entry: MutableStateFlow<ListingMin?> = MutableStateFlow(null)

    val entryState: MutableStateFlow<LoadingState<ListingMin>?> = MutableStateFlow(null)

    fun updateEntry(new: ListingMin) {
        if (entry.value != new) {
            entryState.value = LoadingState()
            entry.value = new
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            entry.flatMapLatest {
                when (it) {
                    is Artist -> repo.getArtistInfo(it.id)
                    is Track -> repo.getTrackInfo(it)
                    else -> TODO()
                }
            }.collect { entryState.update(it) }
        }
    }
}