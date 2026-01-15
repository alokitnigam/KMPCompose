package com.example.kmpcompose.presentation.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.kmpcompose.domain.usecase.*
import com.example.kmpcompose.presentation.mvi.HomeEffect
import com.example.kmpcompose.presentation.mvi.HomeIntent
import com.example.kmpcompose.presentation.mvi.HomeState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeScreenModel(
    private val getNotesUseCase: GetNotesUseCase,
    private val getPinnedNotesUseCase: GetPinnedNotesUseCase,
    private val getArchivedCountUseCase: GetArchivedCountUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val togglePinNoteUseCase: TogglePinNoteUseCase,
    private val archiveNoteUseCase: ArchiveNoteUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effects = Channel<HomeEffect>(Channel.BUFFERED)
    val effects: Flow<HomeEffect> = _effects.receiveAsFlow()

    init {
        loadNotes()
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadNotes -> loadNotes()
            is HomeIntent.AddNote -> screenModelScope.launch {
                _effects.send(HomeEffect.NavigateToDetails(null))
            }
            is HomeIntent.EditNote -> screenModelScope.launch {
                _effects.send(HomeEffect.NavigateToDetails(intent.noteId))
            }
            is HomeIntent.DeleteNote -> deleteNote(intent.noteId)
            is HomeIntent.TogglePin -> togglePin(intent.noteId)
            is HomeIntent.ArchiveNote -> archiveNote(intent.noteId)
            is HomeIntent.NavigateToArchive -> screenModelScope.launch {
                _effects.send(HomeEffect.NavigateToArchive)
            }
        }
    }

    private fun loadNotes() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            combine(
                getPinnedNotesUseCase(),
                getNotesUseCase(),
                getArchivedCountUseCase()
            ) { pinned, all, archivedCount ->
                val normalNotes = all.filter { !it.isPinned }
                Triple(pinned, normalNotes, archivedCount)
            }.catch { error ->
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Unknown error"
                    )
                }
                _effects.send(HomeEffect.ShowError(error.message ?: "Failed to load notes"))
            }.collect { (pinned, normal, archivedCount) ->
                _state.update {
                    it.copy(
                        pinnedNotes = pinned,
                        normalNotes = normal,
                        archivedCount = archivedCount,
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }

    private fun deleteNote(noteId: String) {
        screenModelScope.launch {
            try {
                deleteNoteUseCase(noteId)
            } catch (e: Exception) {
                _effects.send(HomeEffect.ShowError(e.message ?: "Failed to delete note"))
            }
        }
    }

    private fun togglePin(noteId: String) {
        screenModelScope.launch {
            try {
                togglePinNoteUseCase(noteId)
            } catch (e: Exception) {
                _effects.send(HomeEffect.ShowError(e.message ?: "Failed to toggle pin"))
            }
        }
    }

    private fun archiveNote(noteId: String) {
        screenModelScope.launch {
            try {
                archiveNoteUseCase(noteId)
            } catch (e: Exception) {
                _effects.send(HomeEffect.ShowError(e.message ?: "Failed to archive note"))
            }
        }
    }
}
