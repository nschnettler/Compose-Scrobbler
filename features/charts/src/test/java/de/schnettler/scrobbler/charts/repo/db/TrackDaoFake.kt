package de.schnettler.scrobbler.charts.repo.db

import de.schnettler.scrobbler.charts.repo.tools.BaseDaoFake
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.persistence.dao.TrackDao

class TrackDaoFake : TrackDao() {
    private val baseDaoFake = BaseDaoFake<String, LastFmEntity.Track> { it.id }

    override suspend fun insert(obj: LastFmEntity.Track?) = baseDaoFake.insert(obj)

    override suspend fun forceInsert(obj: LastFmEntity.Track?) = baseDaoFake.insert(obj)

    override suspend fun insertAll(obj: List<LastFmEntity.Track?>) = baseDaoFake.insertAll(obj)

    override suspend fun forceInsertAll(obj: List<LastFmEntity.Track>) = baseDaoFake.forceInsertAll(obj)

    override fun update(obj: LastFmEntity.Track) {
        TODO("Not yet implemented")
    }

    override suspend fun updateAll(obj: List<LastFmEntity.Track>) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(obj: LastFmEntity.Track) {
        TODO("Not yet implemented")
    }

    override fun getTrack(id: String, artist: String): LastFmEntity.Track? {
        TODO("Not yet implemented")
    }
}