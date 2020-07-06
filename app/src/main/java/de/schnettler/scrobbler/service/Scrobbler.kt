package de.schnettler.scrobbler.service

import de.schnettler.database.daos.LocalTrackDao
import de.schnettler.database.models.LocalTrack
import de.schnettler.repo.ServiceCoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class Scrobbler @Inject constructor(val dao: LocalTrackDao, private val scope: ServiceCoroutineScope) {
    var currentTrack: LocalTrack? = null

    init {
        scope.launch {
            dao.getCurrentTrack().collect {
                currentTrack = it
            }
        }
    }

    fun saveTrack(track: LocalTrack) {
        scope.launch {
           dao.insertOrUpdatTrack(track)
        }
    }

    fun removeTrack(track: LocalTrack) {
        scope.launch {
            dao.delete(track)
        }
    }

    fun updateTrackAlbum(track: LocalTrack?, album: String) {
        scope.launch {
            track?.let { dao.updateAlbum(album, it.startTime, it.playedBy) }

        }
    }
}