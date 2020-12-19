package de.schnettler.scrobbler.ui.detail.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import de.schnettler.database.models.EntityWithStatsAndInfo.AlbumDetails
import de.schnettler.database.models.LastFmEntity
import de.schnettler.repo.DetailRepository
import de.schnettler.scrobbler.ui.common.compose.RefreshableStateViewModel2

class AlbumViewModel @ViewModelInject constructor(repo: DetailRepository) :
    RefreshableStateViewModel2<LastFmEntity.Album, AlbumDetails, AlbumDetails>(repo.albumStore)