package com.example.assessment.domain.repository

import kotlinx.coroutines.flow.Flow

interface ConnectivityRepository {
    fun observe(): Flow<Status>

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}