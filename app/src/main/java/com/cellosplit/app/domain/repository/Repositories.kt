package com.cellosplit.app.domain.repository

import com.cellosplit.app.domain.model.*
import kotlinx.coroutines.flow.Flow

/** All repository interfaces live in the domain layer — pure Kotlin, no Android. */

interface GroupRepository {
    fun observeAll(): Flow<List<Group>>
    suspend fun getById(id: String): Group?
    suspend fun create(group: Group, members: List<Member>)
    suspend fun update(group: Group)
    suspend fun delete(id: String)
}

interface MemberRepository {
    fun observeByGroup(groupId: String): Flow<List<Member>>
    suspend fun getByGroup(groupId: String): List<Member>
    suspend fun add(member: Member)
    suspend fun update(member: Member)
    suspend fun delete(id: String)
}

interface ExpenseRepository {
    fun observeByGroup(groupId: String): Flow<List<Expense>>
    fun observeTotalByGroup(groupId: String): Flow<Long>
    suspend fun getByGroup(groupId: String): List<Expense>
    suspend fun getById(id: String): Expense?
    suspend fun add(expense: Expense)
    suspend fun delete(id: String)
}

interface PrePaymentRepository {
    fun observeByGroup(groupId: String): Flow<List<PrePayment>>
    suspend fun getByGroup(groupId: String): List<PrePayment>
    suspend fun add(prePayment: PrePayment)
    suspend fun delete(id: String)
}

interface SettlementRepository {
    fun observeByGroup(groupId: String): Flow<List<Settlement>>
    fun observeUnpaidCount(groupId: String): Flow<Int>
    suspend fun replaceAll(groupId: String, settlements: List<Settlement>)
    suspend fun markPaid(id: String, paidAt: Long, upiRef: String?)
}
