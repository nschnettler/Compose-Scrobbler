package de.schnettler.scrobbler.details.ui.album

import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.scrobbler.core.ui.viewmodel.RefreshableStateViewModel2
import de.schnettler.scrobbler.details.model.AlbumDetailEntity
import de.schnettler.scrobbler.details.repo.AlbumDetailRepository
import de.schnettler.scrobbler.model.LastFmEntity
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(repo: AlbumDetailRepository) :
    RefreshableStateViewModel2<LastFmEntity.Album, AlbumDetailEntity, AlbumDetailEntity>(repo.albumStore)