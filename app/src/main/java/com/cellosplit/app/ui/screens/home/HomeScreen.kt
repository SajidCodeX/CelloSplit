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
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
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
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToGroup: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    val groups = when (uiState) {
        is HomeUiState.Success -> (uiState as HomeUiState.Success).groups
        else -> emptyList()
    }

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
                    onClick = { viewModel.createTestGroup() },
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
                items(groups) { group ->
                    GroupRow(
                        modifier = Modifier.clickable { onNavigateToGroup(group.id) },
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
