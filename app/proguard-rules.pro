# CelloSplit ProGuard Rules

# ─── Keep Room entities (R8 must not rename @Entity classes) ─────────────────
-keep class com.cellosplit.app.data.local.entity.** { *; }

# ─── Keep Room DAOs (accessed via reflection by Room) ────────────────────────
-keep interface com.cellosplit.app.data.local.dao.** { *; }

# ─── Keep SQLCipher (native library wrapper) ─────────────────────────────────
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }

# ─── Hilt (do not obfuscate generated Hilt classes) ─────────────────────────
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# ─── Kotlinx Serialization ───────────────────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class * { @kotlinx.serialization.Serializable *; }

# ─── Kotlin ──────────────────────────────────────────────────────────────────
-keep class kotlin.Metadata { *; }

# ─── Coroutines ──────────────────────────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# ─── Timber (only DebugTree used in debug — stripped in release) ──────────────
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
}

# ─── WorkManager ─────────────────────────────────────────────────────────────
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# ─── UPI Intent (android.content.Intent must not be obfuscated) ─────────────
-keep class android.content.Intent { *; }
