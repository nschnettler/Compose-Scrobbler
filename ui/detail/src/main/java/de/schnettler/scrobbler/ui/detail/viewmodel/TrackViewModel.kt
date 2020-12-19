package de.schnettler.scrobbler.ui.detail.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import de.schnettler.database.models.EntityWithStatsAndInfo.TrackWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.repo.DetailRepository
import de.schnettler.scrobbler.ui.common.compose.RefreshableStateViewModel2

class TrackViewModel @ViewModelInject constructor(repo: DetailRepository) :
    RefreshableStateViewModel2<LastFmEntity.Track, TrackWithStatsAndInfo, TrackWithStatsAndInfo>(repo.trackStore)