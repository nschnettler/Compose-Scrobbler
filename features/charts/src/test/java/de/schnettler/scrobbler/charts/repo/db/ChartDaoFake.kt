package de.schnettler.scrobbler.charts.repo.db

import de.schnettler.scrobbler.charts.dao.ChartDao
import de.schnettler.scrobbler.charts.repo.tools.BaseDaoFake
import de.schnettler.scrobbler.model.TopListArtist
import de.schnettler.scrobbler.model.TopListEntry
import de.schnettler.scrobbler.model.TopListTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ChartDaoFake : ChartDao() {
    private val baseDaoFake = BaseDaoFake<String, TopListEntry> { "${it.entityType}_${it.listType}_${it.index}" }

    override fun getTopArtists(): Flow<List<TopListArtist>> {
        return flowOf(emptyList())
    }

    override fun getTopTracks(): Flow<List<TopListTrack>> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(obj: TopListEntry?) = baseDaoFake.insert(obj)

    override suspend fun forceInsert(obj: TopListEntry?) = baseDaoFake.forceInsert(obj)

    override suspend fun insertAll(obj: List<TopListEntry?>) = baseDaoFake.insertAll(obj)

    override suspend fun forceInsertAll(obj: List<TopListEntry>) = baseDaoFake.forceInsertAll(obj)

    override fun update(obj: TopListEntry) {
        TODO("Not yet implemented")
    }

    override suspend fun updateAll(obj: List<TopListEntry>) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(obj: TopListEntry) {
        TODO("Not yet implemented")
    }
}