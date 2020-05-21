package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.*
import de.schnettler.database.models.ListingMin
import de.schnettler.repo.Repository
import timber.log.Timber

class DetailViewModel(val repo: Repository, entry: ListingMin? = null) : ViewModel() {
    private val entryLive = MutableLiveData<ListingMin>(entry)

    val artistInfo = Transformations.switchMap(entryLive) {
        repo.getArtistInfo(it.name).asLiveData(viewModelScope.coroutineContext)
    }

    val albums = Transformations.switchMap(entryLive) {
        repo.getArtistAlbums(it.name).asLiveData(viewModelScope.coroutineContext)
    }


    fun updateEntry(new: ListingMin) {
        if (entryLive.value != new) {
            Timber.d("Value changed. New value: $new")
            entryLive.value = new
        }
    }
}