package com.cellosplit.app.data.local.dao

import androidx.room.*
import com.cellosplit.app.data.local.entity.MemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {

    @Query("SELECT * FROM members WHERE groupId = :groupId ORDER BY name ASC")
    fun observeByGroup(groupId: String): Flow<List<MemberEntity>>

    @Query("SELECT * FROM members WHERE groupId = :groupId ORDER BY name ASC")
    suspend fun getByGroup(groupId: String): List<MemberEntity>

    @Query("SELECT * FROM members WHERE id = :id")
    suspend fun getById(id: String): MemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(members: List<MemberEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(member: MemberEntity)

    @Update
    suspend fun update(member: MemberEntity)

    @Query("DELETE FROM members WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM members WHERE syncedAt IS NULL")
    suspend fun getUnsynced(): List<MemberEntity>

    @Query("UPDATE members SET syncedAt = :timestamp WHERE id = :id")
    suspend fun markSynced(id: String, timestamp: Long)
}
