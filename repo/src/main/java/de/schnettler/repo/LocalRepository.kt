package de.schnettler.repo

import de.schnettler.database.daos.LocalTrackDao
import javax.inject.Inject

class LocalRepository @Inject constructor(
    private val localTrackDao: LocalTrackDao
) {
    fun getData() = localTrackDao.getLocalTracks()
}