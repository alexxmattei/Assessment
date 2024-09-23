package com.example.assessment

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assessment.data.storage.DataStoreHelper
import com.example.assessment.domain.models.UserProfileModel
import com.example.assessment.domain.repository.ConnectivityRepository
import com.example.assessment.domain.repository.UserRepository
import com.example.assessment.domain.util.Resource
import com.example.assessment.ui.screens.UserProfileUiModel
import com.example.assessment.ui.screens.UserSearchManager
import com.example.assessment.ui.screens.UserSearchState
import com.example.assessment.ui.screens.toUserProfileUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userSearchManager: UserSearchManager,
    private val dataStoreHelper: DataStoreHelper,
    private val connectivityRepository: ConnectivityRepository
) : ViewModel() {
    var state by mutableStateOf(UserSearchState())
    var networkState by mutableStateOf(ConnectivityRepository.Status.Unchecked)
    private var searchJob: Job? = null

    init {
        initializeSearchManager()
        loadUserData()
        observeNetworkState()
    }

    private fun observeNetworkState() = viewModelScope.launch {
        connectivityRepository.observe().collect { status ->
            networkState = status
        }
    }

    fun loadUserData() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )

            Log.i("NETWORK_STATE", "Network state is: ${networkState.name}")
            when (networkState) {
                ConnectivityRepository.Status.Available -> {
                    getRemoteUsers()
                }

                else -> {
                    getLocalUsers()
                }
            }
        }
    }

    private suspend fun getRemoteUsers() {
        userRepository.getAllUsers().let { result ->
            when (result) {
                is Resource.Success -> {
                    Log.i("GET_REMOTE_USERS_STATUS", "Get remote users status is SUCCESS")
                    val userProfiles: MutableList<UserProfileModel> = mutableListOf()
                    val userUiProfiles: List<UserProfileUiModel> = coroutineScope {
                        result.data?.map { user ->
                            async {
                                val profile = userRepository.getUserProfile(user.username)
                                profile.data?.let { userProfiles.add(profile.data) }
                                profile
                            }
                        }?.awaitAll()?.mapNotNull {
                            it.data?.toUserProfileUiModel(UserSearchManager.USERS_NAMESPACE)
                        } ?: emptyList()
                    }
                    Log.i(
                        "GET_REMOTE_USERS_CALLED",
                        "Retrieved these users from github: $userProfiles"
                    )
                    userSearchManager.putUsers(userUiProfiles)
                    dataStoreHelper.saveUsers(userProfiles)
                    state = state.copy(
                        users = userUiProfiles,
                        isLoading = false,
                        error = null
                    )
                }

                is Resource.Error -> {
                    Log.i("GET_REMOTE_USERS_STATUS", "Get remote users status is ERROR")
                    getLocalUsers(result.message)
                }
            }
        }
    }

    private fun getLocalUsers(error: String? = null) {
        dataStoreHelper.getUsers().map { users ->
            val localUsers = users.map { user ->
                user.toUserProfileUiModel(UserSearchManager.USERS_NAMESPACE)
            }
            state = state.copy(
                users = localUsers,
                isLoading = false,
                error = error
            )
            Log.i("GET_LOCAL_USERS", "Retrieved these users from datastore: $localUsers")
        }
    }

    fun onSearchQueryChange(query: String) {
        state = state.copy(searchQuery = query)

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            val searchedUsers = userSearchManager.searchUserProfiles(query)
            state = state.copy(users = searchedUsers)
        }
    }

    private fun initializeSearchManager() = viewModelScope.launch {
        userSearchManager.init()
    }

    override fun onCleared() {
        userSearchManager.closeSession()
        super.onCleared()
    }
}