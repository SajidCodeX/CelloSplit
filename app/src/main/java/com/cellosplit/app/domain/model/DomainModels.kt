package com.cellosplit.app.domain.model

/**
 * Pure domain model for a group. No Room or Android imports.
 * All amounts in paise throughout the domain layer.
 */
data class Group(
    val id: String,
    val name: String,
    val description: String,
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long
)

data class Member(
    val id: String,
    val groupId: String,
    val name: String,
    val upiId: String?,
    val avatarColor: String
)

enum class SplitMode { EQUAL, CUSTOM }

data class Expense(
    val id: String,
    val groupId: String,
    val description: String,
    val totalPaise: Long,
    val paidByMemberId: String,
    val splitMode: SplitMode,
    /** memberId → paise owed. Populated for CUSTOM; empty for EQUAL (computed at runtime). */
    val customShares: Map<String, Long> = emptyMap(),
    val createdAt: Long
)

data class PrePayment(
    val id: String,
    val groupId: String,
    val memberId: String,
    val amountPaise: Long,
    val note: String,
    val createdAt: Long
)

data class Settlement(
    val id: String,
    val groupId: String,
    val fromMemberId: String,   // owes
    val toMemberId: String,     // is owed
    val amountPaise: Long,
    val isPaid: Boolean,
    val paidAt: Long?,
    val upiTransactionRef: String?,
    val createdAt: Long
)

/**
 * A summary of a member's balance state in a group.
 * Positive = others owe this member; Negative = this member owes others.
 */
data class Balance(
    val memberId: String,
    val memberName: String,
    val netPaise: Long          // positive = credit, negative = debit
)
