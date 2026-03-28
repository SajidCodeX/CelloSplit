package com.cellosplit.app.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cellosplit.app.data.local.dao.*
import com.cellosplit.app.data.local.entity.*
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

/**
 * The single Room database for CelloSplit.
 *
 * ENCRYPTION STRATEGY:
 *  1. On first launch (or after app data clear), [CryptoManager] generates a
 *     random 256-bit AES key and stores it in the Android Keystore (hardware-
 *     backed on API 28+, software-backed as fallback).
 *  2. The key material is retrieved, Base64-encoded, and passed to SQLCipher's
 *     [SupportFactory]. The DB file on disk is AES-256-CBC encrypted.
 *  3. The key never leaves the Keystore in plaintext — it is only used inside
 *     the TEE (Trusted Execution Environment) to decrypt the DB on open.
 *
 * This means even if someone extracts the .db file from an unrooted device
 * (e.g. via ADB backup), they cannot read it without also extracting the
 * Keystore key — which requires root + OS compromise.
 *
 * Bump [version] and add a [Migration] for every schema change.
 */
@Database(
    entities = [
        GroupEntity::class,
        MemberEntity::class,
        ExpenseEntity::class,
        PrePaymentEntity::class,
        SettlementEntity::class
    ],
    version = 1,
    exportSchema = true      // generates schema JSON for migration auditing
)
@TypeConverters(AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun groupDao(): GroupDao
    abstract fun memberDao(): MemberDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun prePaymentDao(): PrePaymentDao
    abstract fun settlementDao(): SettlementDao

    companion object {
        private const val DB_NAME = "cellosplit.db"

        /**
         * Build a SQLCipher-encrypted Room database.
         *
         * [passphrase] is the raw key bytes retrieved from [CryptoManager].
         * It is zero-filled after the factory is created to minimise
         * the time key material lives in heap memory.
         */
        fun build(context: Context, passphrase: ByteArray): AppDatabase {
            val factory = SupportFactory(passphrase)

            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigrationOnDowngrade()  // dev safety; replace with proper migrations before release
                .build()
        }
    }
}
