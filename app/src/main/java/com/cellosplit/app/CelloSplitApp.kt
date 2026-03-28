package com.cellosplit.app

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.cellosplit.app.core.sync.HiltWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Application class, entry point for Hilt dependency injection.
 * Also initialises Timber logging (debug only) and WorkManager
 * with a Hilt-aware factory so workers can have injected deps.
 */
@HiltAndroidApp
class CelloSplitApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // Timber: logs only in debug builds — zero log output in release
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    /**
     * Provide WorkManager with the Hilt-aware worker factory so that
     * workers annotated with @HiltWorker can receive injected dependencies.
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
