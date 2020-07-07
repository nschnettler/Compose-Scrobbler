package de.schnettler.repo

import de.schnettler.database.daos.LocalTrackDao
import de.schnettler.database.models.LocalTrack
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScrobbleRepository @Inject constructor(
    private val localTrackDao: LocalTrackDao,
    private val scope: ServiceCoroutineScope
) {
    var currentTrack: LocalTrack? = null

    init {
        scope.launch {
            localTrackDao.getCurrentTrack().collect {
                currentTrack = it
            }
        }
    }

    fun saveTrack(track: LocalTrack) {
        scope.launch {
            localTrackDao.insertOrUpdatTrack(track)
        }
    }

    fun removeTrack(track: LocalTrack) {
        scope.launch {
            localTrackDao.delete(track)
        }
    }

    fun updateTrackAlbum(track: LocalTrack?, album: String) {
        scope.launch {
            track?.let { localTrackDao.updateAlbum(album, it.startTime, it.playedBy) }

        }
    }
}