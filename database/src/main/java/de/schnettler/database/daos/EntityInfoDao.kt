package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.database.models.EntityInfo
import kotlinx.coroutines.flow.Flow

@Dao
abstract class EntityInfoDao : BaseDao<EntityInfo> {
    @Query("SELECT * FROM entity_info WHERE id = :id")
    abstract fun getEntityInfo(id: String): Flow<EntityInfo?>
}