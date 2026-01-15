package com.example.kmpcompose.domain.usecase

import com.example.kmpcompose.data.repository.NoteRepository
import com.example.kmpcompose.domain.model.Note
import kotlinx.coroutines.flow.Flow

class GetArchivedNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> = repository.getArchivedNotes()
}
