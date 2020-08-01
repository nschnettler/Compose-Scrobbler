package de.schnettler.scrobbler.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.dropbox.android.external.store4.StoreRequest
import de.schnettler.repo.ChartRepository

class ChartsViewModel @ViewModelInject constructor(
    private val repo: ChartRepository
) : ViewModel() {
    val artistResponse by lazy {
        repo.chartArtistsStore.stream(StoreRequest.cached("", true))
    }
}