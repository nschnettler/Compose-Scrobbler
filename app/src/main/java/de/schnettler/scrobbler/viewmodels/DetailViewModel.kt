package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.*
import de.schnettler.database.models.TopListEntry
import de.schnettler.repo.Repository
import timber.log.Timber

class DetailViewModel(val repo: Repository, entry: TopListEntry? = null) : ViewModel() {
    private val entryLive = MutableLiveData<TopListEntry>(entry)

    val details = Transformations.switchMap(entryLive) {
        repo.getArtistInfo(it.title).asLiveData(viewModelScope.coroutineContext)
    }

    fun updateEntry(new: TopListEntry) {
        if (entryLive.value != new) {
            Timber.d("Value changed. New value: $new")
            entryLive.value = new
        }
    }
}