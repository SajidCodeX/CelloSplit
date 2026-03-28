package com.cellosplit.app.data.local.dao

import androidx.room.*
import com.cellosplit.app.data.local.entity.SettlementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettlementDao {

    /** Live stream of settlements for a group — drives the "Settle Up" list. */
    @Query("SELECT * FROM settlements WHERE groupId = :groupId ORDER BY isPaid ASC, amountPaise DESC")
    fun observeByGroup(groupId: String): Flow<List<SettlementEntity>>

    @Query("SELECT * FROM settlements WHERE groupId = :groupId ORDER BY isPaid ASC, amountPaise DESC")
    suspend fun getByGroup(groupId: String): List<SettlementEntity>

    /** Unpaid count — badge on Home screen groups list. */
    @Query("SELECT COUNT(*) FROM settlements WHERE groupId = :groupId AND isPaid = 0")
    fun observeUnpaidCount(groupId: String): Flow<Int>

    /**
     * Replace all settlements for a group atomically.
     * Called after SettlementEngine recalculates due to a new expense.
     */
    @Transaction
    suspend fun replaceAllForGroup(groupId: String, settlements: List<SettlementEntity>) {
        deleteByGroup(groupId)
        insertAll(settlements)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(settlements: List<SettlementEntity>)

    @Query("DELETE FROM settlements WHERE groupId = :groupId")
    suspend fun deleteByGroup(groupId: String)

    /** Called when UPI callback returns success. */
    @Query("""
        UPDATE settlements 
        SET isPaid = 1, paidAt = :paidAt, upiTransactionRef = :upiRef, syncedAt = NULL
        WHERE id = :id
    """)
    suspend fun markPaid(id: String, paidAt: Long, upiRef: String?)

    @Query("SELECT * FROM settlements WHERE syncedAt IS NULL")
    suspend fun getUnsynced(): List<SettlementEntity>

    @Query("UPDATE settlements SET syncedAt = :timestamp WHERE id = :id")
    suspend fun markSynced(id: String, timestamp: Long)
}
