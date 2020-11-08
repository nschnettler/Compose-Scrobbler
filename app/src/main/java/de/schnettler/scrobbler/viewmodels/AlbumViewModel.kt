package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import de.schnettler.database.models.EntityWithStatsAndInfo.AlbumDetails
import de.schnettler.database.models.LastFmEntity
import de.schnettler.repo.DetailRepository

class AlbumViewModel @ViewModelInject constructor(repo: DetailRepository) :
    RefreshableStateViewModel2<LastFmEntity.Album, AlbumDetails, AlbumDetails>(repo.albumStore)