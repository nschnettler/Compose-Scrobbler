package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.fresh
import de.schnettler.database.models.Album
import de.schnettler.database.models.Artist
import de.schnettler.database.models.CommonEntity
import de.schnettler.database.models.CommonTrack
import de.schnettler.database.models.LastFmStatsEntity
import de.schnettler.repo.DetailRepository
import de.schnettler.repo.Result
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.scrobbler.util.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class DetailViewModel @ViewModelInject constructor(
    private val repo: DetailRepository
) : ViewModel() {
    private val entry: MutableStateFlow<CommonEntity?> = MutableStateFlow(null)
    val state: MutableStateFlow<RefreshableUiState<LastFmStatsEntity>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))

    fun updateEntry(new: CommonEntity) {
        if (entry.updateValue(new)) {
            state.value = RefreshableUiState.Success(data = null, loading = true)
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            entry.flatMapLatest { listing ->
                when (listing) {
                    is Artist ->
                        repo.artistStore.stream(StoreRequest.cached(listing.id, true))
                    is CommonTrack ->
                        repo.trackStore.stream(StoreRequest.cached(listing, true))
                    is Album ->
                        repo.albumStore.stream(StoreRequest.cached(listing, true))
                    else ->
                        flowOf(
                            StoreResponse.Error.Message(
                                "Not implemented yet",
                                ResponseOrigin.Cache
                            )
                        )
                }
            }.collect { response ->
                state.update(response)
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun refresh() {
        state.update(Result.Loading)
        viewModelScope.launch {
            val current = state.value.currentData
            try {
                when (current) {
                    is Artist -> repo.artistStore.fresh(current.id)
                    is CommonTrack -> repo.trackStore.fresh(current)
                    is Album -> repo.albumStore.fresh(current)
                }
            } catch (e: Exception) {
                state.update(Result.Error(e))
            }
        }
    }
}