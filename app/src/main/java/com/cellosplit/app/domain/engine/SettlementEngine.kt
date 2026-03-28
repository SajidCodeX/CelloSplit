package com.cellosplit.app.domain.engine

import com.cellosplit.app.domain.model.*
import java.util.PriorityQueue

/**
 * Core settlement algorithm — Minimum Transactions (Greedy Creditor-Debtor).
 *
 * ALGORITHM:
 *   1. Compute each member's net balance in paise:
 *        net = Σ(amounts paid by member) − Σ(amounts owed by member)
 *   2. Apply pre-payments: add to the paying member's "paid" total.
 *   3. Members with net > 0 are creditors (others owe them).
 *      Members with net < 0 are debtors (they owe others).
 *   4. Greedily match the largest debtor with the largest creditor:
 *        payment = min(|debtor.net|, creditor.net)
 *        Both nets shrink by [payment]. Fully settled party removed.
 *   5. Repeat until all nets are zero.
 *
 * COMPLEXITY: O(n log n) — dominated by priority queue operations.
 * OPTIMAL: Produces at most (n−1) transactions for n participants,
 *          which is the mathematical minimum.
 *
 * ARITHMETIC: All computations in Long paise — zero floating-point
 *             precision errors. Rounding is handled by [SplitCalculator].
 */
object SettlementEngine {

    /**
     * @param members    All members in the group.
     * @param expenses   All expenses in the group.
     * @param prePayments Pre-payments made before expenses were logged.
     * @return Minimal list of [SettlementLeg] to clear all debts.
     */
    fun calculate(
        members: List<Member>,
        expenses: List<Expense>,
        prePayments: List<PrePayment>
    ): List<SettlementLeg> {
        // Step 1: net paise per member
        val net = mutableMapOf<String, Long>().also { map ->
            members.forEach { map[it.id] = 0L }
        }

        // Step 2: apply expenses
        for (expense in expenses) {
            // Payer gets credit for what they paid
            net[expense.paidByMemberId] =
                (net[expense.paidByMemberId] ?: 0L) + expense.totalPaise

            // Each member is debited their share
            val shares = SplitCalculator.computeShares(expense, members)
            for ((memberId, sharePaise) in shares) {
                net[memberId] = (net[memberId] ?: 0L) - sharePaise
            }
        }

        // Step 3: apply pre-payments
        for (pp in prePayments) {
            net[pp.memberId] = (net[pp.memberId] ?: 0L) + pp.amountPaise
        }

        // Step 4: greedily minimise transactions
        // Max-heaps: biggest absolute values matched first
        val creditors = PriorityQueue<Pair<String, Long>>(compareByDescending { it.second })
        val debtors   = PriorityQueue<Pair<String, Long>>(compareByDescending { it.second })

        for ((memberId, netPaise) in net) {
            when {
                netPaise > 0L -> creditors.add(Pair(memberId, netPaise))
                netPaise < 0L -> debtors.add(Pair(memberId, -netPaise)) // store as positive
            }
        }

        val legs = mutableListOf<SettlementLeg>()

        while (debtors.isNotEmpty() && creditors.isNotEmpty()) {
            val (debtorId, debtAmount) = debtors.poll()!!
            val (creditorId, creditAmount) = creditors.poll()!!

            val payment = minOf(debtAmount, creditAmount)
            legs.add(SettlementLeg(from = debtorId, to = creditorId, amountPaise = payment))

            val remainingDebt   = debtAmount   - payment
            val remainingCredit = creditAmount - payment

            if (remainingDebt   > 0L) debtors.add(Pair(debtorId, remainingDebt))
            if (remainingCredit > 0L) creditors.add(Pair(creditorId, remainingCredit))
        }

        return legs
    }
}

data class SettlementLeg(
    val from: String,        // member who owes
    val to: String,          // member who is owed
    val amountPaise: Long
)
