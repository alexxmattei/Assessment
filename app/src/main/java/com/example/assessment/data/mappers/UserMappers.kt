package com.example.assessment.data.mappers

import com.example.assessment.data.remote.UserDto
import com.example.assessment.data.remote.UserProfileDto
import com.example.assessment.domain.models.UserModel
import com.example.assessment.domain.models.UserProfileModel

fun UserDto.toUserModel(): UserModel = UserModel(
    username = this.login
)

fun UserProfileDto.toUserProfileModel(): UserProfileModel = UserProfileModel(
    username = this.login,
    name = this.name ?: "",
    url = this.avatarUrl ?: ""
)