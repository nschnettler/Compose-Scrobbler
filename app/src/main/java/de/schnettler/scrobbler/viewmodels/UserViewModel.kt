package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.ListingMin
import de.schnettler.database.models.TopListEntryType
import de.schnettler.repo.Repository
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class UserViewModel(private val repo: Repository) : ViewModel() {

    val artistState by lazy {
        repo.getTopList(TopListEntryType.USER_ARTIST)
    }

    val userInfo by lazy {
        repo.getUserInfo().asLiveData(viewModelScope.coroutineContext)
    }

    val albumState by lazy {
        repo.getTopList(TopListEntryType.USER_ALBUM)
    }

    val trackState by lazy {
        repo.getTopList(TopListEntryType.USER_TRACKS)
    }

    val artistData = artistState.filter {
        it is StoreResponse.Data
    }.map { it.dataOrNull() ?: listOf()}

    val albumData = albumState.filter {
        it is StoreResponse.Data
    }.map { it.dataOrNull() ?: listOf()}

    val trackData = trackState.filter {
        it is StoreResponse.Data
    }.map { it.dataOrNull() ?: listOf()}
}