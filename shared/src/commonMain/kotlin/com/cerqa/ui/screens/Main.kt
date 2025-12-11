package com.cerqa.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.cerqa.viewmodels.MainViewModel
import org.koin.compose.koinInject

@Composable
fun Main(
    mainViewModel: MainViewModel = koinInject()
) {
    // Fetch user data when screen loads
    LaunchedEffect(Unit) {
        println("Main ===== Screen loaded, fetching user data")
        mainViewModel.fetchUser()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Main Screen Content")
    }
}