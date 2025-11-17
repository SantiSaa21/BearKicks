package com.bearkicks.app.features.auth.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.authDataStore by preferencesDataStore(name = "auth_preferences")

class AuthDataStore(private val context: Context) {
    companion object Keys {
        val USER_ID: Preferences.Key<String> = stringPreferencesKey("user_id")
        val EMAIL: Preferences.Key<String> = stringPreferencesKey("email")
        val DISPLAY_NAME: Preferences.Key<String> = stringPreferencesKey("display_name")
        val PHOTO_URL: Preferences.Key<String> = stringPreferencesKey("photo_url")
    }

    fun observeUserId(): Flow<String?> = context.authDataStore.data.map { it[USER_ID] }

    suspend fun saveSession(userId: String, email: String, name: String?, photoUrl: String?) {
        context.authDataStore.edit { prefs ->
            prefs[USER_ID] = userId
            prefs[EMAIL] = email
            name?.let { prefs[DISPLAY_NAME] = it }
            photoUrl?.let { prefs[PHOTO_URL] = it }
        }
    }

    suspend fun clear() {
        context.authDataStore.edit { it.clear() }
    }
}
