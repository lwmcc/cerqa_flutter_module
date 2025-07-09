package com.cerqa.kotlin.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cerqa.kotlin.viewmodels.MainViewModel

@Composable
fun StartScreen(
    viewModel: MainViewModel = viewModel { MainViewModel() }
) {
    Text("larry composable test")
}