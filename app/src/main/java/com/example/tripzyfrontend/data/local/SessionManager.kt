package com.example.tripzyfrontend.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tripzy_session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val dataStore = context.dataStore

    val tokenFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[KEY_JWT_TOKEN]
    }

    val userIdFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[KEY_USER_ID]
    }

    val userNameFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[KEY_USER_NAME]
    }

    val userEmailFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[KEY_USER_EMAIL]
    }

    val userAvatarFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[KEY_USER_AVATAR]
    }

    val isLoggedInFlow: Flow<Boolean> = tokenFlow.map { !it.isNullOrBlank() }

    suspend fun saveSession(token: String, userId: String, name: String, email: String, avatarUrl: String?) {
        dataStore.edit { preferences ->
            preferences[KEY_JWT_TOKEN] = token
            preferences[KEY_USER_ID] = userId
            preferences[KEY_USER_NAME] = name
            preferences[KEY_USER_EMAIL] = email
            if (avatarUrl != null) {
                preferences[KEY_USER_AVATAR] = avatarUrl
            } else {
                preferences.remove(KEY_USER_AVATAR)
            }
        }
    }

    suspend fun getToken(): String? {
        return dataStore.data.first()[KEY_JWT_TOKEN]
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val KEY_JWT_TOKEN = stringPreferencesKey("jwt_token")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_AVATAR = stringPreferencesKey("user_avatar")
    }
}
