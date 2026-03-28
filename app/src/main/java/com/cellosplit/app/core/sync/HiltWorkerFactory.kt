package com.cellosplit.app.core.sync

import androidx.work.DelegatingWorkerFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WorkManager factory that allows @HiltWorker-annotated workers
 * to receive @Inject constructor dependencies.
 *
 * Registered in [CelloSplitApp.workManagerConfiguration].
 * Workers (e.g. SyncWorker) will be added in Chunk 6a.
 */
@Singleton
class HiltWorkerFactory @Inject constructor() : DelegatingWorkerFactory()
