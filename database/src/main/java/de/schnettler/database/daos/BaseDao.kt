package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

@Dao
interface BaseDao<T> {

    /**
     * Insert an object in the database.
     *
     * @param obj the object to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: T?): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun forceInsert(obj: T?): Long

    /**
     * Insert an array of objects in the database.
     *
     * @param obj the objects to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(obj: List<T?>): List<Long>

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
    suspend fun updateAll(obj: List<T>)

    /**
     * Delete an object from the database
     *
     * @param obj the object to be deleted
     */
    @Delete
    suspend fun delete(obj: T)

//    @Transaction
//    suspend fun upsert(obj: T) {
//        val id: Long = insert(obj)
//        if (id == -1L) {
//            // Get old value
//            update(obj)
//        }
//    }
//
//    @Transaction
//    suspend fun upsertAll(objList: List<@JvmSuppressWildcards T>) {
//        val insertResult: List<Long> = insertAll(objList)
//        val updateList = objList.filterIndexed { index, _ -> insertResult[index] == -1L }
//        if (updateList.isNotEmpty()) {
//            updateAll(updateList)
//        }
//    }
}