package com.cellosplit.app.ui.screens.group

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cellosplit.app.core.utils.DateUtils
import com.cellosplit.app.domain.usecase.GetExpensesForGroupUseCase
import com.cellosplit.app.domain.usecase.GetGroupDetailUseCase
import com.cellosplit.app.domain.usecase.GetSettlementsUseCase
import com.cellosplit.app.domain.usecase.MarkSettlementPaidUseCase
import com.cellosplit.app.payments.UpiManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Intent

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    private val getGroupDetailUseCase: GetGroupDetailUseCase,
    private val getExpensesUseCase: GetExpensesForGroupUseCase,
    private val getSettlementsUseCase: GetSettlementsUseCase,
    private val markSettlementPaidUseCase: MarkSettlementPaidUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Usually passed via Navigation arguments. Keeping resilient for dummy testing.
    private val groupId: String = savedStateHandle.get<String>("groupId") ?: ""

    private val _uiState = MutableStateFlow<GroupDetailUiState>(GroupDetailUiState.Loading)
    val uiState: StateFlow<GroupDetailUiState> = _uiState.asStateFlow()

    init {
        if (groupId.isNotEmpty()) {
            loadGroupDetails(groupId)
        }
    }

    private var activeSettlementId: String? = null // For tracking the UPI request

    fun loadGroupDetails(id: String) {
        viewModelScope.launch {
            val (group, members, _) = getGroupDetailUseCase(id)
            if (group == null) {
                _uiState.value = GroupDetailUiState.Error("Group not found")
                return@launch
            }

            // Real-time observation combining Expenses and Settlements
            combine(
                getExpensesUseCase(id),
                getSettlementsUseCase(id)
            ) { expenses, settlements ->
                // Basic balance computation: sum of unpaid settlements where user is owed
                // Hardcoding current user context mapping for now
                val myUnpaidSettlements = settlements.filter { !it.isPaid }
                val totalOwedPaise = myUnpaidSettlements.sumOf { it.amountPaise }
                
                val expenseUiModels = expenses.map { expense ->
                    val memberName = members.find { it.id == expense.paidByMemberId }?.name ?: "Unknown"
                    ExpenseUiModel(
                        id = expense.id,
                        title = expense.description,
                        date = DateUtils.formatMillisToDateTimeString(expense.createdAt),
                        amount = String.format("%,d", expense.totalPaise / 100),
                        paidBy = memberName,
                        icon = Icons.Default.ShoppingCart,
                        isOwedByMe = memberName != "You"
                    )
                }

                GroupDetailUiState.Success(
                    groupName = group.name,
                    totalOwedAmount = String.format("%,d", totalOwedPaise / 100),
                    expenses = expenseUiModels,
                    pendingSettlement = myUnpaidSettlements.firstOrNull() // Take the first one for intent processing
                )
            }.collectLatest { state ->
                _uiState.value = state
            }
        }
    }

    fun initiateSettleUp(onIntentReady: (Intent) -> Unit) {
        val currentState = _uiState.value as? GroupDetailUiState.Success ?: return
        val settlement = currentState.pendingSettlement

        if (settlement != null) {
            this.activeSettlementId = settlement.id
            // Ideally we get the UPI ID from the real member profile
            val intent = UpiManager.createUpiIntent(
                upiId = "test@upi", 
                payeeName = "Group Member",
                amountRupees = settlement.amountPaise / 100.0,
                transactionNote = "Settling up for ${currentState.groupName}",
                transactionRefId = settlement.id.take(10)
            )
            onIntentReady(intent)
        }
    }

    fun handleUpiResult(responseString: String?) {
        val result = UpiManager.parseUpiResponse(responseString)
        val settlementId = activeSettlementId ?: return
        
        // For testing, we can treat even 'Cancelled' as Success if we want to bypass real UPI apps.
        // Let's implement real workflow:
        if (result is com.cellosplit.app.payments.UpiResult.Success) {
            viewModelScope.launch {
                markSettlementPaidUseCase(settlementId, result.transactionId)
                activeSettlementId = null
            }
        } else {
            // Provide UI toast mechanism for failure in production
            activeSettlementId = null
        }
    }
}

sealed class GroupDetailUiState {
    object Loading : GroupDetailUiState()
    data class Success(
        val groupName: String,
        val totalOwedAmount: String,
        val expenses: List<ExpenseUiModel>,
        val pendingSettlement: com.cellosplit.app.domain.model.Settlement? = null
    ) : GroupDetailUiState()
    data class Error(val message: String) : GroupDetailUiState()
}
