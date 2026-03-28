package com.cellosplit.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Records a single settlement leg calculated by [SettlementEngine].
 *
 * [isPaid] flips to true when the UPI callback confirms the transfer.
 * [upiTransactionRef] is populated from the UPI app's response — acts
 * as a lightweight audit trail without storing any banking credentials.
 *
 * Settlements are DERIVED data (computed from expenses + pre-payments)
 * but we persist them so the user can track paid/unpaid status across
 * app sessions — especially important for offline-first.
 */
@Entity(
    tableName = "settlements",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("groupId"), Index("fromMemberId"), Index("toMemberId")]
)
data class SettlementEntity(
    @PrimaryKey val id: String,            // UUID
    val groupId: String,
    val fromMemberId: String,              // owes money
    val toMemberId: String,                // is owed money
    val amountPaise: Long,
    val isPaid: Boolean = false,
    val paidAt: Long? = null,              // epoch ms of UPI callback
    val upiTransactionRef: String? = null, // UPI TR field — for reference only
    val createdAt: Long,
    val syncedAt: Long? = null
)
