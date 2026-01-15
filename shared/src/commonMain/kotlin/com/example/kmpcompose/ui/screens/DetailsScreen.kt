package com.example.kmpcompose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kmpcompose.presentation.details.DetailsScreenModel
import com.example.kmpcompose.presentation.mvi.DetailsEffect
import com.example.kmpcompose.presentation.mvi.DetailsIntent
import org.koin.core.parameter.parametersOf

data class DetailsScreen(val noteId: String? = null) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<DetailsScreenModel> { parametersOf(noteId) }
        val state by screenModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            screenModel.effects.collect { effect ->
                when (effect) {
                    is DetailsEffect.NavigateBack -> navigator.pop()
                    is DetailsEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text(if (noteId == null) "New Note" else "Edit Note") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { screenModel.handleIntent(DetailsIntent.TogglePin) }
                        ) {
                            Icon(
                                imageVector = if (state.isPinned) Icons.Default.Star else Icons.Outlined.StarBorder,
                                contentDescription = if (state.isPinned) "Unpin" else "Pin"
                            )
                        }
                        
                        if (noteId != null) {
                            IconButton(
                                onClick = { screenModel.handleIntent(DetailsIntent.ArchiveNote) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Archive,
                                    contentDescription = "Archive"
                                )
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = state.title,
                    onValueChange = { screenModel.handleIntent(DetailsIntent.UpdateTitle(it)) },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = state.content,
                    onValueChange = { screenModel.handleIntent(DetailsIntent.UpdateContent(it)) },
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    minLines = 10
                )

                Button(
                    onClick = { screenModel.handleIntent(DetailsIntent.SaveNote) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Save")
                    }
                }
            }
        }
    }
}
