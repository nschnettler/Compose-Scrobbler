package de.schnettler.scrobbler.ui.detail.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.database.models.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity.Artist
import de.schnettler.repo.DetailRepository
import de.schnettler.scrobbler.ui.common.compose.RefreshableStateViewModel2
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(repo: DetailRepository) :
    RefreshableStateViewModel2<Artist, ArtistWithStatsAndInfo, ArtistWithStatsAndInfo>(repo.artistStore)