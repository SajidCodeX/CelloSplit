package com.cellosplit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persisted representation of a group (e.g. "Goa Trip").
 *
 * [createdAt] / [updatedAt] are epoch-milliseconds.
 * [syncedAt]  is null until the row has been pushed to the remote backend.
 *             WorkManager uses this to decide what needs uploading.
 */
@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey val id: String,          // UUID generated in use-case layer
    val name: String,
    val description: String = "",
    val createdBy: String,               // Member id of the creator
    val createdAt: Long,
    val updatedAt: Long,
    val syncedAt: Long? = null           // null = pending sync
)
