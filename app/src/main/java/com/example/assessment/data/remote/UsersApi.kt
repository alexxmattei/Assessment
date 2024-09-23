package com.example.assessment.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface UsersApi {

    @GET("users")
    suspend fun getAllUsers(

    ): List<UserDto>

    @GET("users/{username}")
    suspend fun getUserData(
        @Path("username") username: String
    ): UserProfileDto
}