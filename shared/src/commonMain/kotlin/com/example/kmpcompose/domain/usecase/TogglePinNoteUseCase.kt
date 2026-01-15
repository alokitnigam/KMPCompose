package com.example.kmpcompose.domain.usecase

import com.example.kmpcompose.data.repository.NoteRepository

class TogglePinNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: String) {
        repository.togglePin(id)
    }
}
