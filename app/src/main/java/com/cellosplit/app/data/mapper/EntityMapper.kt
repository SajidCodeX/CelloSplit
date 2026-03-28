package com.cellosplit.app.data.mapper

import com.cellosplit.app.data.local.entity.*
import com.cellosplit.app.domain.model.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer

/**
 * Pure functions to map between Room entities and domain models.
 *
 * Keeps the data layer completely isolated — the rest of the app
 * only ever sees domain models, never Room entities.
 */

private val json = Json { ignoreUnknownKeys = true }

// ─── Group ───────────────────────────────────────────────────────────────────

fun GroupEntity.toDomain() = Group(
    id = id, name = name, description = description,
    createdBy = createdBy, createdAt = createdAt, updatedAt = updatedAt
)

fun Group.toEntity(syncedAt: Long? = null) = GroupEntity(
    id = id, name = name, description = description,
    createdBy = createdBy, createdAt = createdAt, updatedAt = updatedAt,
    syncedAt = syncedAt
)

// ─── Member ──────────────────────────────────────────────────────────────────

fun MemberEntity.toDomain() = Member(
    id = id, groupId = groupId, name = name,
    upiId = upiId, avatarColor = avatarColor
)

fun Member.toEntity(syncedAt: Long? = null) = MemberEntity(
    id = id, groupId = groupId, name = name,
    upiId = upiId, avatarColor = avatarColor, syncedAt = syncedAt
)

// ─── Expense ──────────────────────────────────────────────────────────────────

fun ExpenseEntity.toDomain(): Expense {
    val shares: Map<String, Long> = if (customSharesJson != null) {
        json.decodeFromString(
            MapSerializer(String.serializer(), Long.serializer()),
            customSharesJson
        )
    } else emptyMap()

    return Expense(
        id = id, groupId = groupId, description = description,
        totalPaise = totalPaise, paidByMemberId = paidByMemberId,
        splitMode = SplitMode.valueOf(splitMode),
        customShares = shares, createdAt = createdAt
    )
}

fun Expense.toEntity(syncedAt: Long? = null): ExpenseEntity {
    val sharesJson: String? = if (splitMode == SplitMode.CUSTOM && customShares.isNotEmpty()) {
        json.encodeToString(
            MapSerializer(String.serializer(), Long.serializer()),
            customShares
        )
    } else null

    return ExpenseEntity(
        id = id, groupId = groupId, description = description,
        totalPaise = totalPaise, paidByMemberId = paidByMemberId,
        splitMode = splitMode.name, customSharesJson = sharesJson,
        createdAt = createdAt, updatedAt = createdAt, syncedAt = syncedAt
    )
}

// ─── PrePayment ──────────────────────────────────────────────────────────────

fun PrePaymentEntity.toDomain() = PrePayment(
    id = id, groupId = groupId, memberId = memberId,
    amountPaise = amountPaise, note = note, createdAt = createdAt
)

fun PrePayment.toEntity(syncedAt: Long? = null) = PrePaymentEntity(
    id = id, groupId = groupId, memberId = memberId,
    amountPaise = amountPaise, note = note, createdAt = createdAt,
    syncedAt = syncedAt
)

// ─── Settlement ──────────────────────────────────────────────────────────────

fun SettlementEntity.toDomain() = Settlement(
    id = id, groupId = groupId,
    fromMemberId = fromMemberId, toMemberId = toMemberId,
    amountPaise = amountPaise, isPaid = isPaid,
    paidAt = paidAt, upiTransactionRef = upiTransactionRef,
    createdAt = createdAt
)

fun Settlement.toEntity(syncedAt: Long? = null) = SettlementEntity(
    id = id, groupId = groupId,
    fromMemberId = fromMemberId, toMemberId = toMemberId,
    amountPaise = amountPaise, isPaid = isPaid,
    paidAt = paidAt, upiTransactionRef = upiTransactionRef,
    createdAt = createdAt, syncedAt = syncedAt
)
