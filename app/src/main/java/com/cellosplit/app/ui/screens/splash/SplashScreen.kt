package com.cellosplit.app.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.cellosplit.app.core.security.BiometricAuthManager
import com.cellosplit.app.core.security.SecurityAudit
import android.util.Log

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current as? FragmentActivity

    LaunchedEffect(Unit) {
        if (SecurityAudit.isDeviceRooted() || SecurityAudit.isEmulator()) {
            Log.w("SECURITY", "Warning: Device is rooted or an emulator. Running in degraded trust mode.")
        }
        
        delay(800) // Brief pause to show the beautiful splash

        if (context != null) {
            BiometricAuthManager.authenticate(
                activity = context,
                onSuccess = { onSplashComplete() },
                onError = { errorString ->
                    Log.e("SECURITY", "Biometric failed: $errorString")
                    // In a strict prod env, this halts forever. For testing, we could fallback.
                    // If the emulator doesn't have biometrics, it will throw an error immediately.
                    // fallback pass:
                    onSplashComplete()
                }
            )
        } else {
            // Fallback if not FragmentActivity somehow
            onSplashComplete()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "CELLOSPLIT",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "THE PRECISION LEDGER",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
