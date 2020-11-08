package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import de.schnettler.repo.ChartRepository

class ChartsViewModel @ViewModelInject constructor(repo: ChartRepository) : ViewModel() {
    val artistCharts = repo.artistChartPager.cachedIn(viewModelScope)
    val trackCharts = repo.trackChartPager.cachedIn(viewModelScope)
}