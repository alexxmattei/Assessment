package com.example.assessment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assessment.domain.repository.ConnectivityRepository
import com.example.assessment.ui.screens.UserSearchScreen
import com.example.assessment.ui.theme.AssessmentTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
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

                    val connectivityStatus = viewModel
                        .isNetworkConnected
                        .collectAsState(initial = ConnectivityRepository.Status.Unavailable)

                    LaunchedEffect(connectivityStatus.value) {
                        coroutineScope.launch {
                            when(connectivityStatus.value to viewModel.state.users.size) {
                                ConnectivityRepository.Status.Unavailable to 0 -> {
                                    snackBarHostState.showSnackbar(
                                        message = getString(R.string.internet_not_connected_no_data),
                                        actionLabel = getString(R.string.dismiss),
                                        duration = SnackbarDuration.Short
                                    )
                                }
                                ConnectivityRepository.Status.Lost to 0 -> {
                                    snackBarHostState.showSnackbar(
                                        message = getString(R.string.internet_not_connected_no_data),
                                        actionLabel = getString(R.string.dismiss),
                                        duration = SnackbarDuration.Short
                                    )
                                }
                                ConnectivityRepository.Status.Losing to 0 -> {
                                    snackBarHostState.showSnackbar(
                                        message = getString(R.string.internet_not_connected_no_data),
                                        actionLabel = getString(R.string.dismiss),
                                        duration = SnackbarDuration.Short
                                    )
                                }
                                else -> {
                                    viewModel.loadUserData() // when connection is restored (changes state) refresh data
                                }
                            }
                        }
                    }
                    UserSearchScreen(
                        state = viewModel.state,
                        onSearchQueryChange = viewModel::onSearchQueryChange,
                        connectivityStatus = connectivityStatus.value,
                        contentPadding = paddingValues,
                    )
                }
            }
        }
    }
}
