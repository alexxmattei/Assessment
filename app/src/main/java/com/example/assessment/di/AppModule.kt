package com.example.assessment.di

import android.content.Context
import com.example.assessment.data.remote.UsersApi
import com.example.assessment.data.repository.ConnectivityRepositoryImpl
import com.example.assessment.data.storage.DataStoreHelper
import com.example.assessment.domain.repository.ConnectivityRepository
import com.example.assessment.ui.screens.UserSearchManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUsersApi(): UsersApi {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideUserSearchManager(
        @ApplicationContext
        appContext: Context
    ): UserSearchManager {
        return UserSearchManager(appContext)
    }

    @Provides
    @Singleton
    fun provideConnectivityRepository(
        @ApplicationContext
        appContext: Context
    ): ConnectivityRepository {
         return ConnectivityRepositoryImpl(appContext)
    }

    @Provides
    @Singleton
    fun provideDataStoreHelper(
        @ApplicationContext
        appContext: Context
    ): DataStoreHelper {
        return DataStoreHelper(appContext)
    }
}