package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.database.models.EntityInfo
import de.schnettler.database.models.EntityWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.LastFmEntity.Album
import de.schnettler.database.models.LastFmEntity.Artist
import de.schnettler.database.models.LastFmEntity.Track
import de.schnettler.repo.DetailRepository
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.scrobbler.util.freshFrom
import de.schnettler.scrobbler.util.streamFrom
import de.schnettler.scrobbler.util.updateValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class DetailViewModel @ViewModelInject constructor(
    private val repo: DetailRepository
) : ViewModel() {
    private val entry: MutableStateFlow<LastFmEntity?> = MutableStateFlow(null)
    val state: MutableStateFlow<RefreshableUiState<EntityWithStatsAndInfo>> =
        MutableStateFlow(RefreshableUiState.Success(data = null, loading = true))

    fun updateEntry(new: LastFmEntity) {
        if (entry.updateValue(new)) {
            state.value = RefreshableUiState.Success(data = null, loading = true)
        }
    }

    init {
        viewModelScope.launch {
            entry.filterNotNull().collectLatest { listing ->
                when (listing) {
                    is Artist -> state.streamFrom(repo.artistStore, listing)
                    is Track -> state.streamFrom(repo.trackStore, listing)
                    is Album -> state.streamFrom(repo.albumStore, listing)
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            when (val entity = state.value.currentData?.entity ?: return@launch) {
                is Artist -> state.freshFrom(repo.artistStore, entity)
                is Track -> state.freshFrom(repo.trackStore, entity)
                is Album -> state.freshFrom(repo.albumStore, entity)
            }
        }
    }

    fun onToggleLoveTrackClicked(track: Track, info: EntityInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.toggleTrackLikeStatus(track, info)
        }
    }
}