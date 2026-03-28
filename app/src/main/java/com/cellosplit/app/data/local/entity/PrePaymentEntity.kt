package com.cellosplit.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A pre-payment made by a member BEFORE the group expense is logged.
 *
 * Example: Ayaan paid ₹200 at the door before the ₹3270 dinner was
 * recorded. This pre-payment is factored into the settlement engine
 * by adding [amountPaise] to Ayaan's "paid" column, reducing what
 * everyone else owes him.
 *
 * Pre-payments are scoped to a **group**, not to a specific expense,
 * because they often happen before the bill is known.
 */
@Entity(
    tableName = "pre_payments",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("groupId"), Index("memberId")]
)
data class PrePaymentEntity(
    @PrimaryKey val id: String,       // UUID
    val groupId: String,
    val memberId: String,
    val amountPaise: Long,
    val note: String = "",
    val createdAt: Long,
    val syncedAt: Long? = null
)
