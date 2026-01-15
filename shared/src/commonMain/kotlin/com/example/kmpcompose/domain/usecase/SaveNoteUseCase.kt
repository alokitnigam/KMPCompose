package com.example.kmpcompose.domain.usecase

import com.example.kmpcompose.data.repository.NoteRepository
import com.example.kmpcompose.domain.model.Note
import com.example.kmpcompose.util.currentTimeMillis
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

class SaveNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(
        id: String? = null,
        title: String,
        content: String,
        isPinned: Boolean = false,
        isArchived: Boolean = false
    ) {
        val currentTime = currentTimeMillis()
        
        if (id == null) {
            // Create new note
            val newNote = Note(
                id = UUID.generateUUID().toString(),
                title = title,
                content = content,
                createdAt = currentTime,
                updatedAt = currentTime,
                isPinned = isPinned,
                isArchived = isArchived
            )
            repository.insertNote(newNote)
        } else {
            // Update existing note
            val existingNote = repository.getNoteById(id)
            if (existingNote != null) {
                val updatedNote = existingNote.copy(
                    title = title,
                    content = content,
                    updatedAt = currentTime,
                    isPinned = isPinned,
                    isArchived = isArchived
                )
                repository.updateNote(updatedNote)
            }
        }
    }
}
