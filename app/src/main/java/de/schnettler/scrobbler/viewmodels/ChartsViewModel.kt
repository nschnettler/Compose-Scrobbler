package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.Artist
import de.schnettler.repo.Repository
import timber.log.Timber

class ChartsViewModel(val repo: Repository) : ViewModel() {
    private val artistResponse by lazy {
        Timber.d("Loading Artists")
        repo.getTopArtists().asLiveData()
    }

    val topArtists  = MediatorLiveData<List<Artist>>()

    init {
        topArtists.addSource(artistResponse) {response ->
            if (response is StoreResponse.Data)
                topArtists.value = response.value
        }
    }
}