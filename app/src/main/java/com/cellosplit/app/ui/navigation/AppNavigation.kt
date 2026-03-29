package com.cellosplit.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.cellosplit.app.ui.screens.account.AccountScreen
import com.cellosplit.app.ui.screens.group.GroupDetailScreen
import com.cellosplit.app.ui.screens.home.HomeScreen
import com.cellosplit.app.ui.screens.splash.SplashScreen

enum class Screen {
    Splash, Home, GroupDetail, Account
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf(Screen.Splash) }
    var selectedPath by remember { mutableStateOf<String?>(null) } // For group detail

    when (currentScreen) {
        Screen.Splash -> {
            SplashScreen(onSplashComplete = { currentScreen = Screen.Home })
        }
        Screen.Home -> {
            HomeScreen(
                onNavigateToGroup = { groupId ->
                    selectedPath = groupId
                    currentScreen = Screen.GroupDetail
                }
            )
        }
        Screen.GroupDetail -> {
            GroupDetailScreen(
                groupId = selectedPath ?: "",
                onBackClick = { currentScreen = Screen.Home }
            )
        }
        Screen.Account -> {
            AccountScreen()
        }
    }
}
