package com.cellosplit.app.domain

import com.cellosplit.app.domain.model.Expense
import com.cellosplit.app.domain.model.Member
import com.cellosplit.app.domain.model.PrePayment
import com.cellosplit.app.domain.model.SplitMode
import org.junit.Assert.assertEquals
import org.junit.Test

class SettlementEngineTest {

    @Test
    fun calculate_withProvidedCase_appliesPrepaymentCorrectly() {
        val members = listOf(
            Member("ayaan", "Ayaan"),
            Member("harsh", "Harsh"),
            Member("sajid", "Sajid"),
            Member("riya", "Riya"),
            Member("mohan", "Mohan"),
            Member("neha", "Neha")
        )

        val expenses = listOf(
            Expense(
                id = "e1",
                description = "Trip dinner",
                totalPaise = 327000,
                paidByMemberId = "sajid",
                splitMode = SplitMode.EQUAL
            )
        )

        val prePayments = listOf(PrePayment(memberId = "ayaan", amountPaise = 20000))

        val result = SettlementEngine.calculate(members, expenses, prePayments)
        val balancesById = result.balances.associateBy { it.memberId }

        assertEquals(-34500L, balancesById.getValue("ayaan").netPaise)
        assertEquals(-54500L, balancesById.getValue("harsh").netPaise)
        assertEquals(272500L, balancesById.getValue("sajid").netPaise)
    }
}
