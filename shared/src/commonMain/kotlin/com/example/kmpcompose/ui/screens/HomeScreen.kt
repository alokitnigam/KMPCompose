package com.example.kmpcompose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kmpcompose.presentation.home.HomeScreenModel
import com.example.kmpcompose.presentation.mvi.HomeEffect
import com.example.kmpcompose.presentation.mvi.HomeIntent
import com.example.kmpcompose.ui.components.NoteCard

class HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<HomeScreenModel>()
        val state by screenModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            screenModel.effects.collect { effect ->
                when (effect) {
                    is HomeEffect.NavigateToDetails -> {
                        navigator.push(DetailsScreen(effect.noteId))
                    }
                    is HomeEffect.NavigateToArchive -> {
                        navigator.push(ArchiveScreen())
                    }
                    is HomeEffect.ShowError -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("Notes") },
                    actions = {
                        if (state.archivedCount > 0) {
                            IconButton(
                                onClick = { screenModel.handleIntent(HomeIntent.NavigateToArchive) }
                            ) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error
                                ) {
                                    Text(state.archivedCount.toString())
                                }
                                Icon(
                                    imageVector = Icons.Default.Archive,
                                    contentDescription = "Archived (${state.archivedCount})"
                                )
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { screenModel.handleIntent(HomeIntent.AddNote) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Note")
                }
            }
        ) { paddingValues ->
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (state.pinnedNotes.isNotEmpty()) {
                        item {
                            Text(
                                text = "Pinned",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        items(state.pinnedNotes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onClick = { screenModel.handleIntent(HomeIntent.EditNote(note.id)) },
                                onTogglePin = { screenModel.handleIntent(HomeIntent.TogglePin(note.id)) },
                                onArchive = { screenModel.handleIntent(HomeIntent.ArchiveNote(note.id)) },
                                onDelete = { screenModel.handleIntent(HomeIntent.DeleteNote(note.id)) }
                            )
                        }
                    }
                    
                    if (state.normalNotes.isNotEmpty()) {
                        if (state.pinnedNotes.isNotEmpty()) {
                            item {
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                Text(
                                    text = "Notes",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                        
                        items(state.normalNotes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onClick = { screenModel.handleIntent(HomeIntent.EditNote(note.id)) },
                                onTogglePin = { screenModel.handleIntent(HomeIntent.TogglePin(note.id)) },
                                onArchive = { screenModel.handleIntent(HomeIntent.ArchiveNote(note.id)) },
                                onDelete = { screenModel.handleIntent(HomeIntent.DeleteNote(note.id)) }
                            )
                        }
                    }
                    
                    if (state.pinnedNotes.isEmpty() && state.normalNotes.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No notes yet. Tap + to create one!",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
