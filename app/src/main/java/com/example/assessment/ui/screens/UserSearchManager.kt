package com.example.assessment.ui.screens

import android.content.Context
import android.os.Build
import androidx.appsearch.app.AppSearchSession
import androidx.appsearch.app.GetByDocumentIdRequest
import androidx.appsearch.app.PutDocumentsRequest
import androidx.appsearch.app.SearchSpec
import androidx.appsearch.app.SetSchemaRequest
import androidx.appsearch.localstorage.LocalStorage
import androidx.appsearch.platformstorage.PlatformStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class UserSearchManager(
    private val appContext: Context
) {
    private var session: AppSearchSession? = null
    private val isInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)

    suspend fun init() {
        withContext(Dispatchers.IO) {
            val sessionFuture = if(Build.VERSION.SDK_INT >= 31) {
                PlatformStorage.createSearchSessionAsync(
                    PlatformStorage.SearchContext.Builder(
                        appContext,
                        DATABASE_NAME
                    ).build()
                )
            } else {
                LocalStorage.createSearchSessionAsync(
                    LocalStorage.SearchContext.Builder(
                        appContext,
                        DATABASE_NAME
                    ).build()
                )
            }
            val setSchemaRequest = SetSchemaRequest.Builder()
                .addDocumentClasses(UserProfileUiModel::class.java)
                .build()

            session = sessionFuture.get()
            session?.setSchemaAsync(setSchemaRequest)
            isInitialized.value = true
        }
    }

    suspend fun putUsers(userProfiles: List<UserProfileUiModel>): Boolean {
        return withContext(Dispatchers.IO) {
            session?.putAsync(
                PutDocumentsRequest.Builder()
                    .addDocuments(userProfiles)
                    .build()
            )?.get()?.isSuccess == true
        }
    }

    suspend fun searchUserProfiles(query: String): List<UserProfileUiModel> {
        return withContext(Dispatchers.IO) {
            val searchSpec = SearchSpec.Builder()
                .setSnippetCount(10)
                .addFilterNamespaces(USERS_NAMESPACE)
                .setRankingStrategy(SearchSpec.RANKING_STRATEGY_USAGE_COUNT)
                .build()
            val result = session?.search(
                query,
                searchSpec
            ) ?: return@withContext emptyList()

            val page = result.nextPageAsync.get()

            page.mapNotNull {
                if(it.genericDocument.schemaType == UserProfileUiModel::class.java.simpleName) {
                    it.getDocument(UserProfileUiModel::class.java)
                } else null
            }
        }
    }

    fun closeSession() {
        session?.close()
        session = null
    }

    private suspend fun awaitInitialization() {
        if (!isInitialized.value) {
            isInitialized.first { it }
        }
    }

    companion object {
        const val DATABASE_NAME = "userprofileuimodel"
        const val USERS_NAMESPACE = "searched_user_profiles"
    }
}