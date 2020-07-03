package de.schnettler.scrobbler.service

import de.schnettler.database.daos.LocalTrackDao
import de.schnettler.database.models.LocalTrack
import de.schnettler.repo.ServiceCoroutineScope
import kotlinx.coroutines.*
import javax.inject.Inject

class Scrobbler @Inject constructor(val dao: LocalTrackDao, val scope: ServiceCoroutineScope) {

    fun saveTrack(track: LocalTrack) {
        scope.launch {
           dao.insertTrack(track)
        }
    }
}