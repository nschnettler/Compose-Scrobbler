package de.schnettler.scrobbler.charts.repo.db

import de.schnettler.scrobbler.charts.repo.tools.BaseDaoFake
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.persistence.dao.ArtistDao
import kotlinx.coroutines.flow.Flow

class ArtistDaoFake : ArtistDao() {
    private val baseDaoFake = BaseDaoFake<String, LastFmEntity.Artist> { it.id }

    override fun getArtist(id: String): Flow<LastFmEntity.Artist?> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(obj: LastFmEntity.Artist?) = baseDaoFake.insert(obj)

    override suspend fun forceInsert(obj: LastFmEntity.Artist?) = baseDaoFake.forceInsert(obj)

    override suspend fun insertAll(obj: List<LastFmEntity.Artist?>) = baseDaoFake.insertAll(obj)

    override suspend fun forceInsertAll(obj: List<LastFmEntity.Artist>) = baseDaoFake.forceInsertAll(obj)

    override fun update(obj: LastFmEntity.Artist) {
        TODO("Not yet implemented")
    }

    override suspend fun updateAll(obj: List<LastFmEntity.Artist>) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(obj: LastFmEntity.Artist) {
        TODO("Not yet implemented")
    }
}