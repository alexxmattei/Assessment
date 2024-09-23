package com.example.assessment.data.storage

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.assessment.domain.models.UserProfileModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "user_data_store")

class DataStoreHelper(
    appContext: Context
) {
    private val dataStore = appContext.dataStore
    private val gson = Gson()

    companion object {
        private val USER_LIST_KEY = stringPreferencesKey("user_list")
    }

    suspend fun saveUsers(users: List<UserProfileModel>) {
        val jsonString = gson.toJson(users) // Convert list to JSON string
        dataStore.edit { preferences ->
            preferences[USER_LIST_KEY] = jsonString
        }
    }

    fun getUsers(): Flow<List<UserProfileModel>> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.e("DATASTORE_IO_ERROR", "I/O Error when reading from datastore: $exception")
                    emit(emptyPreferences())
                } else {
                    Log.e("DATASTORE_IO_ERROR", "Error when reading from datastore: $exception")
                    throw exception
                }
            }
            .map { preferences ->
                val jsonString = preferences[USER_LIST_KEY] ?: ""
                if (jsonString.isNotEmpty()) {
                    val type = object : TypeToken<List<UserProfileModel>>() {}.type
                    gson.fromJson(jsonString, type)
                } else {
                    emptyList()
                }
            }
    }
}