package com.cellosplit.app.data.local.dao

import androidx.room.*
import com.cellosplit.app.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses WHERE groupId = :groupId ORDER BY createdAt DESC")
    fun observeByGroup(groupId: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE groupId = :groupId ORDER BY createdAt DESC")
    suspend fun getByGroup(groupId: String): List<ExpenseEntity>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getById(id: String): ExpenseEntity?

    /** Sum of all expense totals in a group — used for the group hero stat. */
    @Query("SELECT COALESCE(SUM(totalPaise), 0) FROM expenses WHERE groupId = :groupId")
    fun observeTotalByGroup(groupId: String): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity)

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM expenses WHERE syncedAt IS NULL")
    suspend fun getUnsynced(): List<ExpenseEntity>

    @Query("UPDATE expenses SET syncedAt = :timestamp WHERE id = :id")
    suspend fun markSynced(id: String, timestamp: Long)
}
