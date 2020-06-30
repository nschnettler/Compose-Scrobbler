package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.database.models.Artist
import de.schnettler.database.models.ListingMin
import de.schnettler.database.models.Track
import de.schnettler.repo.DetailRepository
import de.schnettler.scrobbler.model.LoadingState
import de.schnettler.scrobbler.model.update2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class DetailViewModel @ViewModelInject constructor(repo: DetailRepository)  : ViewModel() {
    private val entry: MutableStateFlow<ListingMin?> = MutableStateFlow(null)
    val entryState: MutableStateFlow<LoadingState<ListingMin>> = MutableStateFlow(LoadingState.Initial())

    fun updateEntry(new: ListingMin) {
        entry.updateValue(new)
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            entry.flatMapLatest {listing ->
                when (listing) {
                    is Artist -> repo.getArtistInfo(listing.id)
                    is Track -> repo.getTrackInfo(listing)
                    else -> TODO()
                }
            }.collect {response ->
                entryState.update2(response)
            }
        }
    }
}