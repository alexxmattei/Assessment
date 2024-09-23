package com.example.assessment.ui.screens

data class UserSearchState(
    val users: List<UserProfileUiModel> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)