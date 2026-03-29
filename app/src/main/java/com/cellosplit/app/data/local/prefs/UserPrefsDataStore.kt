package com.cellosplit.app.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPrefsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val dataStore = context.dataStore

    val userProfileNameFlow: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[KEY_USER_PROFILE_NAME]
        }

    val userUpiIdFlow: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[KEY_USER_UPI_ID]
        }

    suspend fun setUserProfile(name: String, upiId: String?) {
        dataStore.edit { preferences ->
            preferences[KEY_USER_PROFILE_NAME] = name
            if (upiId != null) {
                preferences[KEY_USER_UPI_ID] = upiId
            } else {
                preferences.remove(KEY_USER_UPI_ID)
            }
        }
    }

    companion object {
        private val KEY_USER_PROFILE_NAME = stringPreferencesKey("user_profile_name")
        private val KEY_USER_UPI_ID = stringPreferencesKey("user_upi_id")
    }
}
