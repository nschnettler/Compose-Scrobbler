package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import de.schnettler.database.models.TopListArtist
import de.schnettler.repo.ChartRepository

class ChartsViewModel @ViewModelInject constructor(repo: ChartRepository) :
    RefreshableStateViewModel<String, List<TopListArtist>, List<TopListArtist>>(store = repo.chartArtistsStore, "")