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
import com.cellosplit.app.ui.components.ActionPill
import com.cellosplit.app.ui.components.BalanceDisplay
import com.cellosplit.app.ui.components.ExpenseRow

data class ExpenseUiModel(
    val id: String,
    val title: String,
    val date: String,
    val amount: String,
    val paidBy: String,
    val icon: ImageVector,
    val isOwedByMe: Boolean
)

@Composable
fun GroupDetailScreen(
    groupName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dummyExpenses = listOf(
        ExpenseUiModel("1", "Dinner at Pedro's", "Sat, 8:40 PM", "1,200", "Alok", Icons.Default.Fastfood, true),
        ExpenseUiModel("2", "Flight Tickets", "Thu, 10:00 AM", "8,400", "You", Icons.Default.Flight, false),
        ExpenseUiModel("3", "Groceries", "Mon, 2:15 PM", "450", "Alok", Icons.Default.ShoppingCart, true)
    )

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
                amountText = "7,650",
                label = "YOU ARE OWED"
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 3. Action Pillar for Settlement
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                ActionPill(
                    text = "Settle Up",
                    onClick = { /* TODO */ },
                    isPrimary = true,
                    modifier = Modifier.fillMaxWidth(0.6f)
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
                items(dummyExpenses) { expense ->
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
}
