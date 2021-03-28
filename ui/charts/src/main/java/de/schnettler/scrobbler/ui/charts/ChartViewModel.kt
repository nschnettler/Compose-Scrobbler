package de.schnettler.scrobbler.ui.charts

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import de.schnettler.database.models.Toplist
import de.schnettler.scrobbler.ui.common.compose.widget.PagerState
import kotlinx.coroutines.flow.Flow

abstract class ChartViewModel : ViewModel() {
    abstract fun refresh(tab: ChartTab)
    abstract val pagerState: PagerState
    abstract val artistCharts: Flow<PagingData<Toplist>>
    abstract val trackCharts: Flow<PagingData<Toplist>>
}