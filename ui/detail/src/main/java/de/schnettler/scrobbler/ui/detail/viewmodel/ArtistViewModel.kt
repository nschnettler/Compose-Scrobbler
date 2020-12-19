package de.schnettler.scrobbler.ui.detail.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import de.schnettler.database.models.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity.Artist
import de.schnettler.repo.DetailRepository
import de.schnettler.scrobbler.ui.common.compose.RefreshableStateViewModel2

class ArtistViewModel @ViewModelInject constructor(repo: DetailRepository) :
    RefreshableStateViewModel2<Artist, ArtistWithStatsAndInfo, ArtistWithStatsAndInfo>(repo.artistStore)