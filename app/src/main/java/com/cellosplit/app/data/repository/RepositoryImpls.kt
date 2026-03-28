package com.cellosplit.app.data.repository

import com.cellosplit.app.data.local.dao.*
import com.cellosplit.app.data.mapper.*
import com.cellosplit.app.data.local.entity.*
import com.cellosplit.app.domain.model.*
import com.cellosplit.app.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupRepositoryImpl @Inject constructor(
    private val groupDao: GroupDao,
    private val memberDao: MemberDao
) : GroupRepository {

    override fun observeAll(): Flow<List<Group>> =
        groupDao.observeAll().map { it.map(GroupEntity::toDomain) }

    override suspend fun getById(id: String): Group? =
        groupDao.getById(id)?.toDomain()

    /** Creates group and all its members atomically in a single suspend scope. */
    override suspend fun create(group: Group, members: List<Member>) {
        groupDao.insert(group.toEntity())
        memberDao.insertAll(members.map { it.toEntity() })
    }

    override suspend fun update(group: Group) =
        groupDao.update(group.toEntity())

    override suspend fun delete(id: String) =
        groupDao.deleteById(id)
}

@Singleton
class MemberRepositoryImpl @Inject constructor(
    private val memberDao: MemberDao
) : MemberRepository {

    override fun observeByGroup(groupId: String): Flow<List<Member>> =
        memberDao.observeByGroup(groupId).map { it.map(MemberEntity::toDomain) }

    override suspend fun getByGroup(groupId: String): List<Member> =
        memberDao.getByGroup(groupId).map { it.toDomain() }

    override suspend fun add(member: Member) = memberDao.insert(member.toEntity())

    override suspend fun update(member: Member) = memberDao.update(member.toEntity())

    override suspend fun delete(id: String) = memberDao.deleteById(id)
}

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override fun observeByGroup(groupId: String): Flow<List<Expense>> =
        expenseDao.observeByGroup(groupId).map { it.map(ExpenseEntity::toDomain) }

    override fun observeTotalByGroup(groupId: String): Flow<Long> =
        expenseDao.observeTotalByGroup(groupId)

    override suspend fun getByGroup(groupId: String): List<Expense> =
        expenseDao.getByGroup(groupId).map { it.toDomain() }

    override suspend fun getById(id: String): Expense? =
        expenseDao.getById(id)?.toDomain()

    override suspend fun add(expense: Expense) = expenseDao.insert(expense.toEntity())

    override suspend fun delete(id: String) = expenseDao.deleteById(id)
}

@Singleton
class PrePaymentRepositoryImpl @Inject constructor(
    private val prePaymentDao: PrePaymentDao
) : PrePaymentRepository {

    override fun observeByGroup(groupId: String): Flow<List<PrePayment>> =
        prePaymentDao.observeByGroup(groupId).map { it.map(PrePaymentEntity::toDomain) }

    override suspend fun getByGroup(groupId: String): List<PrePayment> =
        prePaymentDao.getByGroup(groupId).map { it.toDomain() }

    override suspend fun add(prePayment: PrePayment) =
        prePaymentDao.insert(prePayment.toEntity())

    override suspend fun delete(id: String) = prePaymentDao.deleteById(id)
}

@Singleton
class SettlementRepositoryImpl @Inject constructor(
    private val settlementDao: SettlementDao
) : SettlementRepository {

    override fun observeByGroup(groupId: String): Flow<List<Settlement>> =
        settlementDao.observeByGroup(groupId).map { it.map(SettlementEntity::toDomain) }

    override fun observeUnpaidCount(groupId: String): Flow<Int> =
        settlementDao.observeUnpaidCount(groupId)

    override suspend fun replaceAll(groupId: String, settlements: List<Settlement>) =
        settlementDao.replaceAllForGroup(groupId, settlements.map { it.toEntity() })

    override suspend fun markPaid(id: String, paidAt: Long, upiRef: String?) =
        settlementDao.markPaid(id, paidAt, upiRef)
}
