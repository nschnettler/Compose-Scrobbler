package de.schnettler.database.daos

import androidx.room.*
import de.schnettler.database.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BaseDao<T> {

    /**
     * Insert an object in the database.
     *
     * @param obj the object to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(obj: T): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun forceInsert(obj: T)

    /**
     * Insert an array of objects in the database.
     *
     * @param obj the objects to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(obj: List<T>): List<Long>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun forceInsertAll(obj: List<T>): List<Long>

    /**
     * Update an object from the database.
     *
     * @param obj the object to be updated
     */
    @Update
    fun update(obj: T)

    @Update
    fun updateAll(obj: List<T>)

    /**
     * Delete an object from the database
     *
     * @param obj the object to be deleted
     */
    @Delete
    fun delete(obj: T)


    @Transaction
    suspend fun upsert(obj: T) {
        val id: Long = insert(obj)
        if (id == -1L) {
            //Get old value
            update(obj)
        }
    }

   @Transaction
    suspend fun upsertAll(objList: List<@JvmSuppressWildcards T>) {
       val insertResult: List<Long> = insertAll(objList)
       val updateList = objList.filterIndexed { index, value ->  insertResult[index] == -1L}
       if (updateList.isNotEmpty()) {
           updateAll(updateList)
       }
   }
}

@Dao
interface BaseRelationsDao<T>: BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelations(relations: List<RelationEntity>)

    @Transaction
    suspend fun insertEntriesWithRelations(entities: List<@JvmSuppressWildcards T>, relations: List<RelationEntity>) {
        insertAll(entities)
        insertRelations(relations)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopListEntries(topListEntries: List<TopListEntry>)

    @Transaction
    suspend fun insertEntitiesWithTopListEntries(entities: List<@JvmSuppressWildcards T>, topListEntries: List<TopListEntry>) {
        insertAll(entities)
        insertTopListEntries(topListEntries)
    }
}