package com.example.covidtracker

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

object PreferenceManager {
    private const val TAG = "PreferenceManager"

    lateinit var dataStore: DataStore<Preferences>
    lateinit var preferencesFlow: Flow<Preferences>

    fun initPreferences(context: Context) {
        dataStore = context.createDataStore("user_search_preferences")
        preferencesFlow = dataStore.data
        GlobalScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.BLUETOOTH_MODE]  = false
            }
        }
    }

    suspend fun updateUserName(userName: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = userName
        }
    }

    suspend fun updateUserID(userID: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userID
        }
    }

    suspend fun getUserId(): String {
        lateinit var userID: String
        dataStore.edit { preferences ->
            userID = preferences[PreferencesKeys.USER_ID]!!
        }
        return userID
    }

    suspend fun getBluetoothMode(): Boolean {
        var bluetoothMode = false
        dataStore.edit { preferences ->
            bluetoothMode = preferences[PreferencesKeys.BLUETOOTH_MODE]!!
        }
        return bluetoothMode
    }


    suspend fun updateStatus(status: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.STATUS] = status
        }
    }

    suspend fun updateBluetoothMode(bluetoothMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BLUETOOTH_MODE] = bluetoothMode
        }
    }

    suspend fun updateLastNotificationsKey(key: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.lastNotification] = key
        }
    }

    suspend fun updateLastNotificationsCount(key: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.lastNotification_counter] = key
        }
    }

    suspend fun updatePhoto(photo: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PHOTO] = photo
        }
    }

    object PreferencesKeys {
        val USER_ID = preferencesKey<String>("user_id")
        val USER_NAME = preferencesKey<String>("user_name")
        val STATUS = preferencesKey<String>("status")
        val BLUETOOTH_MODE = preferencesKey<Boolean>("bluetooth_mode")
        val lastNotification = preferencesKey<Int>("Last-key")
        val lastNotification_counter = preferencesKey<Int>("Last-key-counter")
        val PHOTO = preferencesKey<String>("photo")
    }
}