package de.schnettler.scrobbler.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.schnettler.database.models.TopListEntryType
import de.schnettler.repo.Repository

class UserViewModel(private val repo: Repository) : ViewModel() {

    val artists by lazy {
        repo.getTopList(TopListEntryType.USER_ARTIST)
    }

    val userInfo by lazy {
        repo.getUserInfo().asLiveData(viewModelScope.coroutineContext)
    }

    val userTopAlbums by lazy {
        repo.getTopList(TopListEntryType.USER_ALBUM)
    }

    val topTracks by lazy {
        repo.getTopList(TopListEntryType.USER_TRACKS)
    }
}