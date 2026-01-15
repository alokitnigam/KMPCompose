package com.example.kmpcompose.presentation.details

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.kmpcompose.data.repository.NoteRepository
import com.example.kmpcompose.domain.usecase.ArchiveNoteUseCase
import com.example.kmpcompose.domain.usecase.SaveNoteUseCase
import com.example.kmpcompose.presentation.mvi.DetailsEffect
import com.example.kmpcompose.presentation.mvi.DetailsIntent
import com.example.kmpcompose.presentation.mvi.DetailsState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetailsScreenModel(
    private val noteRepository: NoteRepository,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val archiveNoteUseCase: ArchiveNoteUseCase,
    initialNoteId: String? = null
) : ScreenModel {

    private val _state = MutableStateFlow(DetailsState(noteId = initialNoteId))
    val state: StateFlow<DetailsState> = _state.asStateFlow()

    private val _effects = Channel<DetailsEffect>(Channel.BUFFERED)
    val effects: Flow<DetailsEffect> = _effects.receiveAsFlow()

    init {
        if (initialNoteId != null) {
            loadNote(initialNoteId)
        }
    }

    fun handleIntent(intent: DetailsIntent) {
        when (intent) {
            is DetailsIntent.LoadNote -> loadNote(intent.noteId)
            is DetailsIntent.UpdateTitle -> _state.update { it.copy(title = intent.title) }
            is DetailsIntent.UpdateContent -> _state.update { it.copy(content = intent.content) }
            is DetailsIntent.SaveNote -> saveNote()
            is DetailsIntent.ArchiveNote -> archiveCurrentNote()
            is DetailsIntent.TogglePin -> _state.update { it.copy(isPinned = !it.isPinned) }
        }
    }

    private fun loadNote(noteId: String?) {
        if (noteId == null) return
        
        screenModelScope.launch {
            try {
                val note = noteRepository.getNoteById(noteId)
                if (note != null) {
                    _state.update {
                        it.copy(
                            noteId = note.id,
                            title = note.title,
                            content = note.content,
                            isPinned = note.isPinned,
                            isArchived = note.isArchived
                        )
                    }
                }
            } catch (e: Exception) {
                _effects.send(DetailsEffect.ShowError(e.message ?: "Failed to load note"))
            }
        }
    }

    private fun saveNote() {
        screenModelScope.launch {
            val currentState = _state.value
            if (currentState.title.isBlank() && currentState.content.isBlank()) {
                _effects.send(DetailsEffect.ShowError("Title and content cannot be empty"))
                return@launch
            }

            _state.update { it.copy(isSaving = true) }
            try {
                saveNoteUseCase(
                    id = currentState.noteId,
                    title = currentState.title,
                    content = currentState.content,
                    isPinned = currentState.isPinned,
                    isArchived = currentState.isArchived
                )
                _state.update { it.copy(isSaving = false) }
                _effects.send(DetailsEffect.NavigateBack)
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false) }
                _effects.send(DetailsEffect.ShowError(e.message ?: "Failed to save note"))
            }
        }
    }

    private fun archiveCurrentNote() {
        screenModelScope.launch {
            val noteId = _state.value.noteId
            if (noteId != null) {
                try {
                    archiveNoteUseCase(noteId)
                    _effects.send(DetailsEffect.NavigateBack)
                } catch (e: Exception) {
                    _effects.send(DetailsEffect.ShowError(e.message ?: "Failed to archive note"))
                }
            }
        }
    }
}
