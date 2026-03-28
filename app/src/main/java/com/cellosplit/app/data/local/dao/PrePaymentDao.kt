package com.cellosplit.app.data.local.dao

import androidx.room.*
import com.cellosplit.app.data.local.entity.PrePaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrePaymentDao {

    @Query("SELECT * FROM pre_payments WHERE groupId = :groupId ORDER BY createdAt DESC")
    fun observeByGroup(groupId: String): Flow<List<PrePaymentEntity>>

    @Query("SELECT * FROM pre_payments WHERE groupId = :groupId ORDER BY createdAt DESC")
    suspend fun getByGroup(groupId: String): List<PrePaymentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prePayment: PrePaymentEntity)

    @Query("DELETE FROM pre_payments WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM pre_payments WHERE syncedAt IS NULL")
    suspend fun getUnsynced(): List<PrePaymentEntity>

    @Query("UPDATE pre_payments SET syncedAt = :timestamp WHERE id = :id")
    suspend fun markSynced(id: String, timestamp: Long)
}
