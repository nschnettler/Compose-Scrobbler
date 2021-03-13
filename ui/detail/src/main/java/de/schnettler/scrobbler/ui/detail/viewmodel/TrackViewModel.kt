package de.schnettler.scrobbler.ui.detail.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.database.models.EntityWithStatsAndInfo.TrackWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.repo.DetailRepository
import de.schnettler.scrobbler.ui.common.compose.RefreshableStateViewModel2
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(repo: DetailRepository) :
    RefreshableStateViewModel2<LastFmEntity.Track, TrackWithStatsAndInfo, TrackWithStatsAndInfo>(repo.trackStore)