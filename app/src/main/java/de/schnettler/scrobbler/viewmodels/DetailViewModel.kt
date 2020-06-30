package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.Artist
import de.schnettler.database.models.ListingMin
import de.schnettler.database.models.Track
import de.schnettler.repo.DetailRepository
import de.schnettler.scrobbler.util.LoadingState
import de.schnettler.scrobbler.util.updateState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class DetailViewModel @ViewModelInject constructor(repo: DetailRepository)  : ViewModel() {
    private val entry: MutableStateFlow<ListingMin?> = MutableStateFlow(null)
    val entryState: MutableStateFlow<LoadingState<ListingMin>> = MutableStateFlow(LoadingState.Initial())

    fun updateEntry(new: ListingMin) {
        if (entry.updateValue(new)) {
            entryState.value = LoadingState.Initial()
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            entry.flatMapLatest {listing ->
                when (listing) {
                    is Artist -> repo.getArtistInfo(listing.id)
                    is Track -> repo.getTrackInfo(listing)
                    else -> flowOf(StoreResponse.Error.Message("Not implemented yet", ResponseOrigin.Cache))
                }
            }.collect {response ->
                entryState.updateState(response)
            }
        }
    }
}