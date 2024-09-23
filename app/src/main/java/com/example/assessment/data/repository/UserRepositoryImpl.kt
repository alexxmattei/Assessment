package com.example.assessment.data.repository

import com.example.assessment.data.mappers.toUserModel
import com.example.assessment.data.mappers.toUserProfileModel
import com.example.assessment.data.remote.UsersApi
import com.example.assessment.domain.models.UserModel
import com.example.assessment.domain.models.UserProfileModel
import com.example.assessment.domain.repository.UserRepository
import com.example.assessment.domain.util.Resource
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: UsersApi
): UserRepository {
    override suspend fun getAllUsers(): Resource<List<UserModel>> {
        return try {
            Resource.Success(
                data = api.getAllUsers().map { userDto -> userDto.toUserModel() }
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            Resource.Error(ex.message ?: "Error loading users. Search will not work")
        }
    }

    override suspend fun getUserProfile(username: String): Resource<UserProfileModel> {
        return try {
            Resource.Success(
                data = api.getUserData(username).toUserProfileModel()
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            Resource.Error(ex.message ?: "Error loading user profile")
        }
    }
}