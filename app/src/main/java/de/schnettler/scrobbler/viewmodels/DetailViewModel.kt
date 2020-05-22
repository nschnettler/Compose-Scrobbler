package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.*
import de.schnettler.database.models.Artist
import de.schnettler.database.models.ListingMin
import de.schnettler.repo.Repository
import de.schnettler.scrobbler.model.LoadingState
import de.schnettler.scrobbler.model.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetailViewModel(val repo: Repository) : ViewModel() {
    private val entryLive = MutableLiveData<ListingMin>()

    val entryState: MutableStateFlow<LoadingState<Artist>?> = MutableStateFlow(null)

    private val artistInfo = Transformations.switchMap(entryLive) {
        repo.getArtistInfo(it.id).asLiveData(viewModelScope.coroutineContext)
    }

    fun updateEntry(new: ListingMin) {
        if (entryLive.value != new) {
            entryState.value = LoadingState()
            entryLive.value = new
        }
    }

    init {
        viewModelScope.launch {
            artistInfo.asFlow().collect {
                entryState.update(it)
            }
        }
    }
}