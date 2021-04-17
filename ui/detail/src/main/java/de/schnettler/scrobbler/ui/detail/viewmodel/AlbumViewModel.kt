package de.schnettler.scrobbler.ui.detail.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.repo.DetailRepository
import de.schnettler.scrobbler.core.model.EntityWithStatsAndInfo.AlbumDetails
import de.schnettler.scrobbler.core.model.LastFmEntity
import de.schnettler.scrobbler.core.ui.viewmodel.RefreshableStateViewModel2
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(repo: DetailRepository) :
    RefreshableStateViewModel2<LastFmEntity.Album, AlbumDetails, AlbumDetails>(repo.albumStore)