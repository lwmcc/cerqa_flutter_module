package com.mccartycarclub.repository.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mccartycarclub.domain.UserPreferencesManager
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferences @Inject constructor(
    @ApplicationContext
    private val context: Context
) : UserPreferencesManager {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

    override suspend fun setUserId(userId: String) {
        context.dataStore.edit { settings ->
            settings[PreferenceKeys.USER_ID] = userId
        }
    }

    override fun getUserId(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.USER_ID]
        }
    }
}