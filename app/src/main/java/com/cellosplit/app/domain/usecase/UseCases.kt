package com.cellosplit.app.domain.usecase

import com.cellosplit.app.domain.engine.SettlementEngine
import com.cellosplit.app.domain.model.*
import com.cellosplit.app.domain.repository.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

// ════════════════════════════════════════════════════════════════════════════
// GROUP USE CASES
// ════════════════════════════════════════════════════════════════════════════

class GetGroupsUseCase @Inject constructor(
    private val groupRepo: GroupRepository
) {
    operator fun invoke(): Flow<List<Group>> = groupRepo.observeAll()
}

class GetGroupDetailUseCase @Inject constructor(
    private val groupRepo: GroupRepository,
    private val memberRepo: MemberRepository,
    private val expenseRepo: ExpenseRepository
) {
    suspend operator fun invoke(groupId: String): Triple<Group?, List<Member>, List<Expense>> {
        val group = groupRepo.getById(groupId)
        val members = memberRepo.getByGroup(groupId)
        val expenses = expenseRepo.getByGroup(groupId)
        return Triple(group, members, expenses)
    }
}

class CreateGroupUseCase @Inject constructor(
    private val groupRepo: GroupRepository
) {
    /** [memberNames] — list of names entered by user. UPI IDs added later. */
    suspend operator fun invoke(name: String, description: String, memberNames: List<String>) {
        require(name.isNotBlank()) { "Group name cannot be blank" }
        require(memberNames.size >= 2) { "A group needs at least 2 members" }

        val now = System.currentTimeMillis()
        val creatorId = UUID.randomUUID().toString()
        val groupId = UUID.randomUUID().toString()

        val members = memberNames.map { memberName ->
            Member(
                id = UUID.randomUUID().toString(),
                groupId = groupId,
                name = memberName.trim(),
                upiId = null,
                avatarColor = randomAvatarColor()
            )
        }

        val group = Group(
            id = groupId,
            name = name.trim(),
            description = description.trim(),
            createdBy = creatorId,
            createdAt = now,
            updatedAt = now
        )

        groupRepo.create(group, members)
    }

    private fun randomAvatarColor(): String {
        val colors = listOf(
            "#3A3A6E", "#1E4D6E", "#1E5C3A",
            "#5C3A1E", "#5C1E4D", "#3A5C1E"
        )
        return colors.random()
    }
}

class DeleteGroupUseCase @Inject constructor(
    private val groupRepo: GroupRepository
) {
    suspend operator fun invoke(groupId: String) = groupRepo.delete(groupId)
}

// ════════════════════════════════════════════════════════════════════════════
// EXPENSE USE CASES
// ════════════════════════════════════════════════════════════════════════════

class AddExpenseUseCase @Inject constructor(
    private val expenseRepo: ExpenseRepository,
    private val memberRepo: MemberRepository,
    private val prePaymentRepo: PrePaymentRepository,
    private val settlementRepo: SettlementRepository
) {
    /**
     * Adds an expense and immediately recalculates settlements.
     *
     * [customSharesPaise] — only required for CUSTOM split. Map of memberId→paise.
     * Validates that shares sum equals totalPaise before persisting.
     */
    suspend operator fun invoke(
        groupId: String,
        description: String,
        totalPaise: Long,
        paidByMemberId: String,
        splitMode: SplitMode,
        customSharesPaise: Map<String, Long> = emptyMap()
    ) {
        require(description.isNotBlank()) { "Description cannot be blank" }
        require(totalPaise > 0) { "Amount must be greater than zero" }

        if (splitMode == SplitMode.CUSTOM) {
            val sharesSum = customSharesPaise.values.sum()
            require(sharesSum == totalPaise) {
                "Custom shares (${sharesSum}p) must equal total (${totalPaise}p)"
            }
        }

        val expense = Expense(
            id = UUID.randomUUID().toString(),
            groupId = groupId,
            description = description.trim(),
            totalPaise = totalPaise,
            paidByMemberId = paidByMemberId,
            splitMode = splitMode,
            customShares = customSharesPaise,
            createdAt = System.currentTimeMillis()
        )

        expenseRepo.add(expense)
        recalculateSettlements(groupId)
    }

    private suspend fun recalculateSettlements(groupId: String) {
        val members     = memberRepo.getByGroup(groupId)
        val expenses    = expenseRepo.getByGroup(groupId)
        val prePayments = prePaymentRepo.getByGroup(groupId)

        val legs = SettlementEngine.calculate(members, expenses, prePayments)
        val now  = System.currentTimeMillis()

        val settlements = legs.map { leg ->
            Settlement(
                id = UUID.randomUUID().toString(),
                groupId = groupId,
                fromMemberId = leg.from,
                toMemberId   = leg.to,
                amountPaise  = leg.amountPaise,
                isPaid = false, paidAt = null, upiTransactionRef = null,
                createdAt = now
            )
        }

        settlementRepo.replaceAll(groupId, settlements)
    }
}

class GetExpensesForGroupUseCase @Inject constructor(
    private val expenseRepo: ExpenseRepository
) {
    operator fun invoke(groupId: String): Flow<List<Expense>> =
        expenseRepo.observeByGroup(groupId)
}

class DeleteExpenseUseCase @Inject constructor(
    private val expenseRepo: ExpenseRepository,
    private val memberRepo: MemberRepository,
    private val prePaymentRepo: PrePaymentRepository,
    private val settlementRepo: SettlementRepository
) {
    suspend operator fun invoke(expenseId: String, groupId: String) {
        expenseRepo.delete(expenseId)
        // Recalculate after deletion
        val members     = memberRepo.getByGroup(groupId)
        val expenses    = expenseRepo.getByGroup(groupId)
        val prePayments = prePaymentRepo.getByGroup(groupId)
        val legs = SettlementEngine.calculate(members, expenses, prePayments)
        val now  = System.currentTimeMillis()
        settlementRepo.replaceAll(groupId, legs.map { leg ->
            Settlement(UUID.randomUUID().toString(), groupId, leg.from, leg.to,
                leg.amountPaise, false, null, null, now)
        })
    }
}

// ════════════════════════════════════════════════════════════════════════════
// SETTLEMENT USE CASES
// ════════════════════════════════════════════════════════════════════════════

class GetSettlementsUseCase @Inject constructor(
    private val settlementRepo: SettlementRepository
) {
    operator fun invoke(groupId: String): Flow<List<Settlement>> =
        settlementRepo.observeByGroup(groupId)

    fun unpaidCount(groupId: String): Flow<Int> =
        settlementRepo.observeUnpaidCount(groupId)
}

class MarkSettlementPaidUseCase @Inject constructor(
    private val settlementRepo: SettlementRepository
) {
    suspend operator fun invoke(settlementId: String, upiRef: String?) {
        settlementRepo.markPaid(
            id = settlementId,
            paidAt = System.currentTimeMillis(),
            upiRef = upiRef
        )
    }
}
