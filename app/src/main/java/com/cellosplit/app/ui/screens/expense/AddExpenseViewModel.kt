package com.cellosplit.app.ui.screens.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cellosplit.app.domain.model.SplitMode
import com.cellosplit.app.domain.usecase.AddExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val addExpenseUseCase: AddExpenseUseCase
) : ViewModel() {

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    fun saveExpense(
        groupId: String,
        description: String,
        amountText: String,
        splitMode: SplitMode,
        onSuccess: () -> Unit
    ) {
        if (description.isBlank() || amountText.isBlank()) return

        val amountRupees = amountText.toLongOrNull() ?: 0L
        val totalPaise = amountRupees * 100

        if (totalPaise <= 0) return

        viewModelScope.launch {
            _isSaving.value = true
            try {
                // Hardcode current user paid for now; will update during actual auth logic
                val defaultPaidBy = "user_id_placeholder" 
                
                addExpenseUseCase(
                    groupId = groupId,
                    description = description,
                    totalPaise = totalPaise,
                    paidByMemberId = defaultPaidBy,
                    splitMode = splitMode
                )
                onSuccess()
            } catch (e: Exception) {
                // Handle err
            } finally {
                _isSaving.value = false
            }
        }
    }
}
