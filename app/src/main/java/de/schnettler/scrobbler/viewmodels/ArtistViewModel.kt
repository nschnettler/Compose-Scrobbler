package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import de.schnettler.database.models.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity.Artist
import de.schnettler.repo.DetailRepository

class ArtistViewModel @ViewModelInject constructor(repo: DetailRepository) :
    RefreshableStateViewModel2<Artist, ArtistWithStatsAndInfo, ArtistWithStatsAndInfo>(repo.artistStore)