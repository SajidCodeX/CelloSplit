package com.cellosplit.app.domain.engine

import com.cellosplit.app.domain.model.*

/**
 * Computes each member's share of a single expense.
 *
 * EQUAL SPLIT with paise-accurate rounding:
 *   - Base share = totalPaise / memberCount  (integer division)
 *   - Remainder  = totalPaise % memberCount  paise
 *   - Remainder is distributed one paise per member in member-id order.
 *   - This guarantees: Σ(shares) == totalPaise — always, for any amount.
 *
 * CUSTOM SPLIT:
 *   - Shares are taken directly from expense.customShares.
 *   - The use case layer validates Σ(customShares) == totalPaise before saving.
 *
 * Returns: Map<memberId, sharePaise>
 */
object SplitCalculator {

    fun computeShares(expense: Expense, members: List<Member>): Map<String, Long> {
        return when (expense.splitMode) {
            SplitMode.EQUAL  -> equalShares(expense.totalPaise, members)
            SplitMode.CUSTOM -> expense.customShares.toMap()
        }
    }

    private fun equalShares(totalPaise: Long, members: List<Member>): Map<String, Long> {
        val n = members.size
        if (n == 0) return emptyMap()

        val baseShare = totalPaise / n
        val remainder = (totalPaise % n).toInt() // always 0..n-1

        // Sort deterministically by id so remainder assignment is consistent
        val sorted = members.sortedBy { it.id }

        return sorted.mapIndexed { index, member ->
            // First [remainder] members get one extra paise
            val share = baseShare + if (index < remainder) 1L else 0L
            member.id to share
        }.toMap()
    }
}
