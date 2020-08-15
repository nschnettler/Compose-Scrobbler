package de.schnettler.database.daos

import androidx.room.Dao
import de.schnettler.database.models.EntityInfo

@Dao
abstract class EntityInfoDao : BaseDao<EntityInfo>