package com.example.kmpcompose.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.example.kmpcompose.database.NotesDatabase
import com.example.kmpcompose.domain.model.Note
import com.example.kmpcompose.util.currentTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NoteRepositoryImpl(
    private val database: NotesDatabase
) : NoteRepository {

    private val queries = database.noteQueries

    override fun getAllNotes(): Flow<List<Note>> {
        return queries.selectAllNotes()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { notes -> notes.map { it.toDomainModel() } }
    }

    override fun getPinnedNotes(): Flow<List<Note>> {
        return queries.selectPinnedNotes()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { notes -> notes.map { it.toDomainModel() } }
    }

    override fun getArchivedNotes(): Flow<List<Note>> {
        return queries.selectArchivedNotes()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { notes -> notes.map { it.toDomainModel() } }
    }

    override fun getNoteById(id: String): Note? {
        return queries.selectNoteById(id).executeAsOneOrNull()?.toDomainModel()
    }

    override fun getArchivedCount(): Flow<Long> {
        return queries.countArchivedNotes()
            .asFlow()
            .mapToOne(Dispatchers.Default)
    }

    override suspend fun insertNote(note: Note) = withContext(Dispatchers.Default) {
        queries.insertNote(
            id = note.id,
            title = note.title,
            content = note.content,
            createdAt = note.createdAt,
            updatedAt = note.updatedAt,
            isPinned = if (note.isPinned) 1L else 0L,
            isArchived = if (note.isArchived) 1L else 0L
        )
    }

    override suspend fun updateNote(note: Note) = withContext(Dispatchers.Default) {
        queries.updateNote(
            title = note.title,
            content = note.content,
            updatedAt = note.updatedAt,
            isPinned = if (note.isPinned) 1L else 0L,
            isArchived = if (note.isArchived) 1L else 0L,
            id = note.id
        )
    }

    override suspend fun deleteNote(id: String) = withContext(Dispatchers.Default) {
        queries.deleteNote(id)
    }

    override suspend fun togglePin(id: String) = withContext(Dispatchers.Default) {
        queries.togglePin(
            updatedAt = currentTimeMillis(),
            id = id
        )
    }

    override suspend fun archiveNote(id: String) = withContext(Dispatchers.Default) {
        queries.archiveNote(
            updatedAt = currentTimeMillis(),
            id = id
        )
    }

    override suspend fun restoreNote(id: String) = withContext(Dispatchers.Default) {
        queries.restoreNote(
            updatedAt = currentTimeMillis(),
            id = id
        )
    }

    private fun com.example.kmpcompose.database.Note.toDomainModel(): Note {
        return Note(
            id = id,
            title = title,
            content = content,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isPinned = isPinned == 1L,
            isArchived = isArchived == 1L
        )
    }
}
