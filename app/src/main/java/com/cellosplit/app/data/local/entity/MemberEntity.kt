package com.cellosplit.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A member belonging to a group.
 *
 * Foreign key to [GroupEntity] ensures members are deleted when
 * their group is deleted (CASCADE).
 * [upiId] is nullable — user can add it later in Account settings.
 */
@Entity(
    tableName = "members",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("groupId")]
)
data class MemberEntity(
    @PrimaryKey val id: String,          // UUID
    val groupId: String,
    val name: String,
    val upiId: String? = null,
    val avatarColor: String = "#2C2C2E", // hex — shown as circle background
    val syncedAt: Long? = null
)
