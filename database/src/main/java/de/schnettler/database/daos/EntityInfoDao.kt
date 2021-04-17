package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.scrobbler.core.model.EntityInfo

@Dao
abstract class EntityInfoDao : BaseDao<EntityInfo> {
    @Query("SELECT * FROM entity_info WHERE id = :id")
    abstract fun get(id: String): EntityInfo
}