# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep Room generated classes and properties
-keep class androidx.room.** { *; }

# Keep SQLCipher crypto dependencies
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }

# Keep Domain Models used by Room
-keepclassmembers class com.cellosplit.app.domain.model.** { *; }
-keep class com.cellosplit.app.domain.model.** { *; }

# Keep Compose and Hilt essentials
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
-keep class dagger.hilt.** { *; }

# Obfuscate everything else heavily
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic
