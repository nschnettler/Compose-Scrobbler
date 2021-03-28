package de.schnettler.scrobbler.ui.charts

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.database.models.Toplist
import de.schnettler.repo.ChartRepository
import de.schnettler.scrobbler.ui.common.compose.widget.PagerState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ChartViewModelImpl @Inject constructor(private val repo: ChartRepository) : ChartViewModel() {
    override val artistCharts: Flow<PagingData<Toplist>> = repo.artistChartPager.cachedIn(viewModelScope)
    override val trackCharts = repo.trackChartPager.cachedIn(viewModelScope)

    override val pagerState: PagerState = PagerState()

    override fun refresh(tab: ChartTab) {
//        viewModelScope.launch {
//            when (tab) {
//                ChartTab.Track -> trackState.freshFrom(repo.chartTrackStore, "")
//                ChartTab.Artist -> artistState.freshFrom(repo.chartArtistsStore, "")
//            }
//        }
    }
}