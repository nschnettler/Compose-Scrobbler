package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.*
import de.schnettler.repo.DetailRepository
import de.schnettler.scrobbler.util.LoadingState
import de.schnettler.scrobbler.util.updateState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class DetailViewModel @ViewModelInject constructor(repo: DetailRepository)  : ViewModel() {
    private val entry: MutableStateFlow<CommonEntity?> = MutableStateFlow(null)
    val entryState: MutableStateFlow<LoadingState<LastFmStatsEntity>> = MutableStateFlow(LoadingState.Initial())

    fun updateEntry(new: CommonEntity) {
        if (entry.updateValue(new)) {
            entryState.value = LoadingState.Initial()
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            entry.flatMapLatest {listing ->
                when (listing) {
                    is Artist -> repo.getArtistInfo(listing.id)
                    is CommonTrack -> repo.getTrackInfo(listing)
                    is Album -> repo.getAlbumInfo(listing)
                    else -> flowOf(StoreResponse.Error.Message("Not implemented yet", ResponseOrigin.Cache))
                }
            }.collect {response ->
                entryState.updateState(response)
            }
        }
    }
}