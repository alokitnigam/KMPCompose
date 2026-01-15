package com.example.kmpcompose.presentation.archive

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.kmpcompose.domain.usecase.DeleteNoteUseCase
import com.example.kmpcompose.domain.usecase.GetArchivedNotesUseCase
import com.example.kmpcompose.domain.usecase.RestoreNoteUseCase
import com.example.kmpcompose.presentation.mvi.ArchiveEffect
import com.example.kmpcompose.presentation.mvi.ArchiveIntent
import com.example.kmpcompose.presentation.mvi.ArchiveState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArchiveScreenModel(
    private val getArchivedNotesUseCase: GetArchivedNotesUseCase,
    private val restoreNoteUseCase: RestoreNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(ArchiveState())
    val state: StateFlow<ArchiveState> = _state.asStateFlow()

    private val _effects = Channel<ArchiveEffect>(Channel.BUFFERED)
    val effects: Flow<ArchiveEffect> = _effects.receiveAsFlow()

    init {
        loadArchivedNotes()
    }

    fun handleIntent(intent: ArchiveIntent) {
        when (intent) {
            is ArchiveIntent.LoadArchivedNotes -> loadArchivedNotes()
            is ArchiveIntent.RestoreNote -> restoreNote(intent.noteId)
            is ArchiveIntent.DeleteNote -> deleteNote(intent.noteId)
        }
    }

    private fun loadArchivedNotes() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            getArchivedNotesUseCase()
                .catch { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Unknown error"
                        )
                    }
                    _effects.send(ArchiveEffect.ShowError(error.message ?: "Failed to load archived notes"))
                }
                .collect { notes ->
                    _state.update {
                        it.copy(
                            archivedNotes = notes,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    private fun restoreNote(noteId: String) {
        screenModelScope.launch {
            try {
                restoreNoteUseCase(noteId)
                _effects.send(ArchiveEffect.ShowMessage("Note restored"))
            } catch (e: Exception) {
                _effects.send(ArchiveEffect.ShowError(e.message ?: "Failed to restore note"))
            }
        }
    }

    private fun deleteNote(noteId: String) {
        screenModelScope.launch {
            try {
                deleteNoteUseCase(noteId)
                _effects.send(ArchiveEffect.ShowMessage("Note deleted"))
            } catch (e: Exception) {
                _effects.send(ArchiveEffect.ShowError(e.message ?: "Failed to delete note"))
            }
        }
    }
}
