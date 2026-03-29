package com.cellosplit.app.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class SettlementEngineTest {

    private val engine = SettlementEngine()

    @Test
    fun `test exact match offsets`() {
        val balances = mapOf(
            "A" to -5000L, // Owes 50.00
            "B" to 5000L   // Owed 50.00
        )

        val settlements = engine.calculateOptimalSettlements(balances)
        
        assertEquals(1, settlements.size)
        assertEquals("A", settlements.first().fromMemberId)
        assertEquals("B", settlements.first().toMemberId)
        assertEquals(5000L, settlements.first().amountPaise)
    }

    @Test
    fun `test multi way split`() {
        val balances = mapOf(
            "A" to -800L,
            "B" to -200L,
            "C" to 1000L
        )

        val settlements = engine.calculateOptimalSettlements(balances)
        
        assertEquals(2, settlements.size)
        // A owes C 800
        val aToC = settlements.find { it.fromMemberId == "A" && it.toMemberId == "C" }
        assertEquals(800L, aToC?.amountPaise)
        // B owes C 200
        val bToC = settlements.find { it.fromMemberId == "B" && it.toMemberId == "C" }
        assertEquals(200L, bToC?.amountPaise)
    }

    @Test
    fun `test complex chain reduction`() {
        // A owes B 100
        // B owes C 100
        val balances = mapOf(
            "A" to -100L,
            "B" to 0L,
            "C" to 100L
        )

        val settlements = engine.calculateOptimalSettlements(balances)
        
        assertEquals(1, settlements.size)
        // Engine should simplify so A pays C directly
        assertEquals("A", settlements.first().fromMemberId)
        assertEquals("C", settlements.first().toMemberId)
        assertEquals(100L, settlements.first().amountPaise)
    }
}
