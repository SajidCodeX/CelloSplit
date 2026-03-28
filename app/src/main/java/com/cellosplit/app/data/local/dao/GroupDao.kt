package com.cellosplit.app.data.local.dao

import androidx.room.*
import com.cellosplit.app.data.local.entity.GroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    /** Real-time stream of all groups, newest first. */
    @Query("SELECT * FROM groups ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<GroupEntity>>

    /** One-shot read — for sync and export use cases. */
    @Query("SELECT * FROM groups ORDER BY updatedAt DESC")
    suspend fun getAll(): List<GroupEntity>

    @Query("SELECT * FROM groups WHERE id = :id")
    fun observeById(id: String): Flow<GroupEntity?>

    @Query("SELECT * FROM groups WHERE id = :id")
    suspend fun getById(id: String): GroupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: GroupEntity)

    @Update
    suspend fun update(group: GroupEntity)

    @Query("DELETE FROM groups WHERE id = :id")
    suspend fun deleteById(id: String)

    /** Rows not yet pushed to backend — used by SyncWorker. */
    @Query("SELECT * FROM groups WHERE syncedAt IS NULL")
    suspend fun getUnsynced(): List<GroupEntity>

    /** Mark a row as synced after successful backend upload. */
    @Query("UPDATE groups SET syncedAt = :timestamp WHERE id = :id")
    suspend fun markSynced(id: String, timestamp: Long)
}
