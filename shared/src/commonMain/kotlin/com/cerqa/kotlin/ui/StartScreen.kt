package com.cerqa.kotlin.ui

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cerqa.kotlin.viewmodels.MainViewModel

@Composable
fun StartScreen(
    onNavHostReady: suspend (NavController) -> Unit = {},
    viewModel: MainViewModel = viewModel { MainViewModel() },
) {
    val navController = rememberNavController()

    Scaffold(bottomBar = { BottomBar(navController) }) {
        NavHost(navController = navController, startDestination = "main") {
            composable("main") {

            }
        }
    }

    LaunchedEffect(navController) {
        onNavHostReady(navController)
    }

    Text(
        text = "larry composable test",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onTertiary,
    )
}