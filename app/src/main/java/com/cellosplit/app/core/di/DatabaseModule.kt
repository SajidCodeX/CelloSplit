package com.cellosplit.app.core.di

import android.content.Context
import com.cellosplit.app.core.security.CryptoManager
import com.cellosplit.app.data.local.dao.*
import com.cellosplit.app.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        cryptoManager: CryptoManager
    ): AppDatabase {
        val passphrase = cryptoManager.getOrCreateDbKey()
        return AppDatabase.build(context, passphrase).also {
            passphrase.fill(0) // zero key material immediately after DB is opened
        }
    }

    @Provides fun provideGroupDao(db: AppDatabase): GroupDao = db.groupDao()
    @Provides fun provideMemberDao(db: AppDatabase): MemberDao = db.memberDao()
    @Provides fun provideExpenseDao(db: AppDatabase): ExpenseDao = db.expenseDao()
    @Provides fun providePrePaymentDao(db: AppDatabase): PrePaymentDao = db.prePaymentDao()
    @Provides fun provideSettlementDao(db: AppDatabase): SettlementDao = db.settlementDao()
}
