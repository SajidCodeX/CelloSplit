package com.cellosplit.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cellosplit.app.ui.components.ActionPill
import com.cellosplit.app.ui.components.BalanceDisplay
import com.cellosplit.app.ui.components.FloatingNavPill
import com.cellosplit.app.ui.components.GroupRow

// Temporary dummy model until ViewModel integration
data class GroupUiModel(
    val id: String,
    val name: String,
    val subtitle: String,
    val initials: String,
    val balance: String,
    val isPositive: Boolean
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToGroup: (String) -> Unit = {}
) {
    val dummyGroups = listOf(
        GroupUiModel("1", "Goa Trip", "Changed 2h ago", "GT", "2,450", true),
        GroupUiModel("2", "Apartment 4B", "Rent & Groceries", "A4", "1,200", false)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(64.dp)) // Top safe area + extra breathing room

            // 1. Hero Balance
            BalanceDisplay(
                amountText = "4,250",
                label = "TOTAL BALANCE"
            )

            Spacer(modifier = Modifier.height(48.dp)) // Luxury whitespace

            // 2. Action Pillars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionPill(
                    text = "Scan to Pay",
                    onClick = { /* TODO */ },
                    isPrimary = true,
                    modifier = Modifier.weight(1f)
                )
                ActionPill(
                    text = "New Group",
                    onClick = { /* TODO */ },
                    isPrimary = false,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 3. Section Header
            Text(
                text = "RECENT GROUPS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Groups List (No dividers per Stitch rules)
            LazyColumn(
                contentPadding = PaddingValues(bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dummyGroups) { group ->
                    GroupRow(
                        title = group.name,
                        subtitle = group.subtitle,
                        initials = group.initials,
                        trailingContent = {
                            Text(
                                text = "₹${group.balance}",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (group.isPositive) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }
        }

        // Floating Bottom Navigation
        FloatingNavPill(
            items = listOf(
                Pair(Icons.Default.Home) { /* TODO */ },
                Pair(Icons.Default.Add) { /* TODO */ },
                Pair(Icons.Default.Person) { /* TODO */ }
            ),
            selectedIndex = 0,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}
