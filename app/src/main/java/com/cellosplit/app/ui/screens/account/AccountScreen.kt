package com.cellosplit.app.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cellosplit.app.ui.components.GroupRow

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "ACCOUNT",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "PROFILE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            GroupRow(
                title = "Alok Nath",
                subtitle = "alok@example.com",
                initials = "AN"
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "SECURITY",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            GroupRow(
                title = "Biometric Lock",
                subtitle = "Enabled",
                initials = "🔒"
            )
            GroupRow(
                title = "UPI Setup",
                subtitle = "Linked",
                initials = "💰"
            )
        }
    }
}
