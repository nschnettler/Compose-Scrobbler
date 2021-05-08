package de.schnettler.scrobbler.details.ui.artist

import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.scrobbler.core.ui.viewmodel.RefreshableStateViewModel2
import de.schnettler.scrobbler.details.repo.ArtistDetailRepository
import de.schnettler.scrobbler.model.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.scrobbler.model.LastFmEntity.Artist
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(repo: ArtistDetailRepository) :
    RefreshableStateViewModel2<Artist, ArtistWithStatsAndInfo, ArtistWithStatsAndInfo>(repo.artistStore)