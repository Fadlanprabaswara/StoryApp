package com.example.storyapp.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class UserPrefence private constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
        @Volatile
        private var INSTANCE: UserPrefence? = null
        private val NAME = stringPreferencesKey("name")
        private val TOKEN = stringPreferencesKey("token")
        private val STATE = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): UserPrefence {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPrefence(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    fun getUser(): Flow<UserModel> {
        val nameFlow = dataStore.data.map { preferences ->
            preferences[NAME] ?: ""
        }

        val tokenFlow = dataStore.data.map { preferences ->
            preferences[TOKEN] ?: ""
        }

        val stateFlow = dataStore.data.map { preferences ->
            preferences[STATE] ?: false
        }

        return combine(nameFlow, tokenFlow, stateFlow) { name, token, state ->
            UserModel(name, token, state)
        }
    }


    suspend fun saveSession(session: UserModel) {
        dataStore.edit { preferences ->
            preferences[NAME] = session.name
            preferences[TOKEN] = session.token
            preferences[STATE] = session.isLogin
        }
    }

    suspend fun login() {
        dataStore.edit { preferences ->
            preferences[STATE] = true
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }


}