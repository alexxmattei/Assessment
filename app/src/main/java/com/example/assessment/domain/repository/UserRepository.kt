package com.example.assessment.domain.repository

import com.example.assessment.domain.models.UserModel
import com.example.assessment.domain.models.UserProfileModel
import com.example.assessment.domain.util.Resource

interface UserRepository {
    suspend fun getAllUsers(): Resource<List<UserModel>>

    suspend fun getUserProfile(username: String): Resource<UserProfileModel>
}