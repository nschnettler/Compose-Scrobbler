package de.schnettler.scrobbler.ui.detail.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.repo.DetailRepository
import de.schnettler.scrobbler.model.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.scrobbler.model.LastFmEntity.Artist
import de.schnettler.scrobbler.core.ui.viewmodel.RefreshableStateViewModel2
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(repo: DetailRepository) :
    RefreshableStateViewModel2<Artist, ArtistWithStatsAndInfo, ArtistWithStatsAndInfo>(repo.artistStore)