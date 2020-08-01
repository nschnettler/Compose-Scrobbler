package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.fresh
import de.schnettler.database.models.EntityWithStatsAndInfo
import de.schnettler.database.models.EntityWithStatsAndInfo.TrackWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.LastFmEntity.Album
import de.schnettler.database.models.LastFmEntity.Artist
import de.schnettler.database.models.LastFmEntity.Track
import de.schnettler.repo.DetailRepository
import de.schnettler.repo.Result
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.scrobbler.util.update
import de.schnettler.scrobbler.util.updateValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailViewModel @ViewModelInject constructor(
    private val repo: DetailRepository
) : ViewModel() {
    private val entry: MutableStateFlow<LastFmEntity?> = MutableStateFlow(null)
    val state: MutableStateFlow<RefreshableUiState<EntityWithStatsAndInfo>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))

    fun updateEntry(new: LastFmEntity) {
        Timber.d("Entry updated $new")
        if (entry.updateValue(new)) {
            state.value = RefreshableUiState.Success(data = null, loading = true)
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            entry.flatMapLatest { listing ->
                when (listing) {
                    is Artist ->
                        repo.artistStore.stream(StoreRequest.cached(listing, true))
                    is Track ->
                        repo.trackStore.stream(StoreRequest.cached(listing, true))
                    is Album ->
                        repo.albumStore.stream(StoreRequest.cached(listing, true))
                    null -> TODO()
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
                when (val entity = current?.entity) {
                    is Artist -> repo.artistStore.fresh(entity)
                    is Track -> repo.trackStore.fresh(entity)
                    is Album -> repo.albumStore.fresh(entity)
                }
            } catch (e: Exception) {
                state.update(Result.Error(e))
            }
        }
    }
}