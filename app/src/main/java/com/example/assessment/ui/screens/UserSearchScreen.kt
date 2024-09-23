package com.example.assessment.ui.screens

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.assessment.R
import com.example.assessment.domain.repository.ConnectivityRepository

@Composable
fun UserSearchScreen(
    state: UserSearchState,
    onSearchQueryChange: (String) -> Unit,
    connectivityStatus: ConnectivityRepository.Status,
    contentPadding: PaddingValues
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text(stringResource(R.string.search_by_nickname)) },
            trailingIcon = {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search_icon))
                }
            },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = contentPadding
        ) {
            items(state.users) { user ->
                UserSearchItem(
                    userProfile = user
                )
            }
        }
    }
}

@Composable
private fun UserSearchItem(
    modifier: Modifier = Modifier,
    userProfile: UserProfileUiModel
) {
    var userBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val localContext = LocalContext.current

    LaunchedEffect(userProfile.url) {
        Glide.with(localContext)
            .asBitmap()
            .load(userProfile.url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    userBitmap = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            userBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = stringResource(R.string.user_profile_image),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Github name: ${userProfile.name}", fontSize = 14.sp)
            Text(text = "Online nickname: ${userProfile.username}", fontSize = 12.sp)
        }
    }
}