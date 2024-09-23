package com.example.assessment.ui.screens

import androidx.appsearch.annotation.Document
import androidx.appsearch.app.AppSearchSchema
import com.example.assessment.domain.models.UserProfileModel
import java.util.UUID

@Document
data class UserProfileUiModel(
    @Document.Namespace
    val namespace: String = UserSearchManager.USERS_NAMESPACE,
    @Document.Id
    val id: String,
    @Document.Score
    val score: Int,
    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES)
    val username: String,
    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES)
    val name: String,
    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES)
    val url: String
)

fun UserProfileModel.toUserProfileUiModel(namespace: String) = UserProfileUiModel(
    namespace = namespace,
    id = UUID.randomUUID().toString(), // local id for search indexing
    score = 1,
    username = this.username,
    name = this.name ?: "",
    url = this.url ?: ""
)