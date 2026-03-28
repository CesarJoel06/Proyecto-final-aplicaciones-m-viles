package com.cesar.securityquotes.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session_store")

class SessionStore(private val context: Context) {
    companion object {
        val TOKEN = stringPreferencesKey("token")
        val USERNAME = stringPreferencesKey("username")
        val EMAIL = stringPreferencesKey("email")
        val IMAGE_URL = stringPreferencesKey("image_url")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[TOKEN] }
    val usernameFlow: Flow<String?> = context.dataStore.data.map { it[USERNAME] }
    val emailFlow: Flow<String?> = context.dataStore.data.map { it[EMAIL] }
    val imageUrlFlow: Flow<String?> = context.dataStore.data.map { it[IMAGE_URL] }

    suspend fun saveSession(token: String, username: String, email: String, imageUrl: String?) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN] = token
            prefs[USERNAME] = username
            prefs[EMAIL] = email
            if (imageUrl != null) prefs[IMAGE_URL] = imageUrl
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
