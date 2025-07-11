package com.cerqa.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cerqa.viewmodels.MainViewModel
import com.cerqa.ui.components.navItems

@Composable
fun StartScreen(
    onNavHostReady: suspend (NavController) -> Unit = {},
    mainViewModel: MainViewModel = viewModel { MainViewModel() },
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(
                items = navItems,
                currentRoute = navController.currentBackStackEntry?.destination?.route,
                onBottomNavClick = { route ->
                    navController.navigate(route)
                },
            )
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "larry composable test",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Button(onClick = {
                mainViewModel.getUserData()
            }) {
                Text("Prefs Read/Write Test")
            }
        }

        NavHost(navController = navController, startDestination = "main") {
            composable("main") {

            }

            composable("chat") {

            }

            composable("notifications") {

            }
        }
    }

    LaunchedEffect(navController) {
        onNavHostReady(navController)
    }
}