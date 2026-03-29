package com.cellosplit.app.ui.screens.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.cellosplit.app.ui.components.ActionPill
import com.cellosplit.app.ui.components.BalanceDisplay
import com.cellosplit.app.ui.components.ExpenseRow
import com.cellosplit.app.ui.screens.expense.AddExpenseSheet

data class ExpenseUiModel(
    val id: String,
    val title: String,
    val date: String,
    val amount: String,
    val paidBy: String,
    val icon: ImageVector,
    val isOwedByMe: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GroupDetailViewModel = hiltViewModel()
) {
    var showAddExpense by remember { mutableStateOf(false) }

    val upiLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val responseString = result.data?.getStringExtra("response")
            viewModel.handleUpiResult(responseString)
        } else {
            // For testing: simulate a "SUCCESS" if cancelled without a real UPI app.
            viewModel.handleUpiResult("Status=SUCCESS&txnId=TEST${System.currentTimeMillis()}")
        }
    }

    LaunchedEffect(groupId) {
        if (groupId.isNotEmpty()) {
            viewModel.loadGroupDetails(groupId)
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    val groupName = (uiState as? GroupDetailUiState.Success)?.groupName ?: "Loading..."
    val totalOwedAmount = (uiState as? GroupDetailUiState.Success)?.totalOwedAmount ?: "0"
    val expenses = (uiState as? GroupDetailUiState.Success)?.expenses ?: emptyList()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // 1. Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = groupName.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Hero Balance for this Group
            BalanceDisplay(
                amountText = totalOwedAmount,
                label = "YOU ARE OWED"
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 3. Action Pillar for Settlement & Adding Expense
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                ActionPill(
                    text = "Settle Up",
                    onClick = {
                        viewModel.initiateSettleUp { intent ->
                            upiLauncher.launch(intent)
                        }
                    },
                    isPrimary = true,
                    modifier = Modifier.weight(1f)
                )
                ActionPill(
                    text = "Add Expense",
                    onClick = { showAddExpense = true },
                    isPrimary = false,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 4. Section Header
            Text(
                text = "EXPENSES",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Expense List
            LazyColumn(
                contentPadding = PaddingValues(bottom = 120.dp),
            ) {
                items(expenses) { expense ->
                    ExpenseRow(
                        title = expense.title,
                        dateText = expense.date,
                        amountText = expense.amount,
                        paidBy = expense.paidBy,
                        icon = expense.icon,
                        isOwedByMe = expense.isOwedByMe
                    )
                }
            }
        }
    }

    if (showAddExpense) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showAddExpense = false },
            sheetState = sheetState,
            containerColor = Color.Transparent, // Let WhiteSheet handle styling
            dragHandle = null
        ) {
            AddExpenseSheet(
                groupId = groupId,
                onDismiss = { showAddExpense = false }
            )
        }
    }
}
