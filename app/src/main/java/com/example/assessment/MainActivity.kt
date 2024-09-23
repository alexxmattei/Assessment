package com.example.assessment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.assessment.domain.repository.ConnectivityRepository
import com.example.assessment.ui.screens.UserSearchScreen
import com.example.assessment.ui.theme.AssessmentTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AssessmentTheme {
                val snackBarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()
                Scaffold(
                    snackbarHost = { SnackbarHost(snackBarHostState) },
                    modifier = Modifier.fillMaxSize()
                ) { paddingValues ->

                    val connectivityStatus = viewModel.networkState

                    LaunchedEffect(connectivityStatus) {
                        coroutineScope.launch {
                            val areUsersLocallyStored = viewModel.state.users.isNotEmpty()
                            when(connectivityStatus) {
                                ConnectivityRepository.Status.Unchecked -> { }
                                ConnectivityRepository.Status.Available -> {
                                    viewModel.loadUserData() // when connection is restored (changes state) refresh data
                                    snackBarHostState.showSnackbar(
                                        message = getString(R.string.syncing_users),
                                        actionLabel = getString(R.string.dismiss),
                                        duration = SnackbarDuration.Short
                                    )
                                }
                                else -> {
                                    if(!areUsersLocallyStored) {
                                        snackBarHostState.showSnackbar(
                                            message = getString(R.string.internet_not_connected_no_data),
                                            actionLabel = getString(R.string.dismiss),
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            }
                        }
                    }
                    UserSearchScreen(
                        state = viewModel.state,
                        onSearchQueryChange = viewModel::onSearchQueryChange,
                        contentPadding = paddingValues,
                    )
                }
            }
        }
    }
}
