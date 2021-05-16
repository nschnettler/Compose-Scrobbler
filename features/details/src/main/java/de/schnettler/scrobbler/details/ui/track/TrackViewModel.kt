package de.schnettler.scrobbler.details.ui.track

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.scrobbler.core.ui.viewmodel.RefreshableStateViewModel2
import de.schnettler.scrobbler.details.model.TrackDetailEntity
import de.schnettler.scrobbler.details.repo.TrackDetailRepository
import de.schnettler.scrobbler.model.EntityInfo
import de.schnettler.scrobbler.model.LastFmEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(
    private val repo: TrackDetailRepository
) : RefreshableStateViewModel2<LastFmEntity.Track, TrackDetailEntity, TrackDetailEntity>(repo.trackStore) {
    fun onToggleLoveTrackClicked(track: LastFmEntity.Track, info: EntityInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.toggleTrackLikeStatus(track, info)
        }
    }
}