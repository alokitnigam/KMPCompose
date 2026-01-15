package com.example.kmpcompose.domain.usecase

import com.example.kmpcompose.data.repository.NoteRepository

class DeleteNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: String) {
        repository.deleteNote(id)
    }
}
