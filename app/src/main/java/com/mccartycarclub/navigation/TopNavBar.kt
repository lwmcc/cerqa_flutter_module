package com.mccartycarclub.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cerqa.navigation.AppDestination
import com.cerqa.ui.TopNavItem
import com.mccartycarclub.R
import com.mccartycarclub.ui.viewmodels.SearchViewModel
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    currentRoute: String?,
    text: String,
    items: List<TopNavItem>,
    searchViewModel: SearchViewModel,
    onNavClick: () -> Unit,
    onBackClick: () -> Unit,
    onTopNavClick: (String) -> Unit,
    onQueryChanged: (String) -> Unit,
) {

    var input by remember { mutableStateOf("") }
    var clearSearchVisible by remember { mutableStateOf(false) }

    TopAppBar(
        navigationIcon = {
            if (currentRoute.equals( AppDestination.Main.route)) {
                IconButton(onClick = onNavClick) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
                }
            } else {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        title = {
            when (currentRoute) {
                AppDestination.Main.route -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background),
                    ) {
                        TextField(
                            value = input,
                            onValueChange = onQueryChanged,
                            placeholder = { Text(text = text) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                errorContainerColor = Color.Transparent,
                            )
                        )

                        if (clearSearchVisible) {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = stringResource(id = R.string.text_field_clear),
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .clickable {
                                        input = ""
                                        searchViewModel.onQueryChange("")
                                    },
                            )
                        }
                    }
                }

                AppDestination.ContactsSearch.route -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background),
                    ) {
                        TextField(
                            value = input,
                            maxLines = 2,
                            onValueChange = {
                                input = it
                                searchViewModel.onQueryChange(it)
                                clearSearchVisible = input.isNotEmpty()
                            },
                            label = {
                                Text(
                                    text = stringResource(id = R.string.user_search),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                errorContainerColor = Color.Transparent,
                            )
                        )

                        if (clearSearchVisible) {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = stringResource(id = R.string.text_field_clear),
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .clickable {
                                        input = ""
                                        searchViewModel.onQueryChange("")
                                    },
                            )
                        }
                    }

                }
            }
        },
        actions = {
            items.forEach { item ->
                IconButton(onClick = {
                    onTopNavClick(item.route)
                }) {
                    Icon(item.icon, contentDescription = item.contentDescription)
                }
            }
        }
    )
}