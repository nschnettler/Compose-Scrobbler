package de.schnettler.database.daos

import androidx.room.Dao
import de.schnettler.database.models.TopListEntry

@Dao
abstract class TopListDao : BaseDao<TopListEntry>