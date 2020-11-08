package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import de.schnettler.database.models.EntityWithStatsAndInfo.TrackWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.repo.DetailRepository

class TrackViewModel @ViewModelInject constructor(repo: DetailRepository) :
    RefreshableStateViewModel2<LastFmEntity.Track, TrackWithStatsAndInfo, TrackWithStatsAndInfo>(repo.trackStore)