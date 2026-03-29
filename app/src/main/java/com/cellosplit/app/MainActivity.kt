package com.cellosplit.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.cellosplit.app.ui.navigation.AppNavigation
import com.cellosplit.app.ui.theme.CelloSplitTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The single Activity of the app.
 *
 * Security measures applied here:
 *  - FLAG_SECURE  → prevents screenshots, screen recording, and
 *    appearance in the recent-apps thumbnail. Set immediately so
 *    it is active before any UI is drawn.
 *  - Edge-to-edge  → lets our dark gradient fill behind the status bar.
 *
 * The biometric gate lives in the Nav graph (SplashScreen) so it can
 * be cleanly observed as a composable state rather than inside Activity
 * lifecycle callbacks.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // FLAG_SECURE must be set BEFORE super.onCreate / setContent
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CelloSplitTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}
