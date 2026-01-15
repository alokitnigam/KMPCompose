package com.example.kmpcompose.domain.usecase

import com.example.kmpcompose.data.repository.NoteRepository

class RestoreNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: String) {
        repository.restoreNote(id)
    }
}
