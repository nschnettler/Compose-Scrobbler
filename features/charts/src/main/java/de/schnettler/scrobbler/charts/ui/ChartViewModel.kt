package de.schnettler.scrobbler.charts.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.scrobbler.charts.repo.ChartRepository
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(repo: ChartRepository) : ViewModel() {
//    val artistState: Flow<PagingData<TopListArtist>> = flow { emit(PagingData()) }

//    val trackState: Flow<PagingData<TopListTrack>> = repo.chartTrackPager.cachedIn(viewModelScope)
}