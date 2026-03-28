package com.cellosplit.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A single expense within a group.
 *
 * Amounts are stored in **paise** (Long) — no floating-point arithmetic
 * anywhere in the financial logic. 100 paise = ₹1.
 *
 * [splitMode] is stored as a String ("EQUAL" | "CUSTOM") — Room's
 * TypeConverter converts to/from the domain enum.
 *
 * [customSharesJson] holds a JSON map of memberId→paise for CUSTOM splits.
 * It is null for EQUAL splits (shares are computed at runtime).
 */
@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("groupId"), Index("paidByMemberId")]
)
data class ExpenseEntity(
    @PrimaryKey val id: String,           // UUID
    val groupId: String,
    val description: String,
    val totalPaise: Long,                 // ₹545.00 → 54500L
    val paidByMemberId: String,
    val splitMode: String,                // "EQUAL" | "CUSTOM"
    val customSharesJson: String? = null, // JSON only for CUSTOM splits
    val createdAt: Long,
    val updatedAt: Long,
    val syncedAt: Long? = null
)
