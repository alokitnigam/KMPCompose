package com.example.kmpcompose.data.repository

import com.example.kmpcompose.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getPinnedNotes(): Flow<List<Note>>
    fun getArchivedNotes(): Flow<List<Note>>
    fun getNoteById(id: String): Note?
    fun getArchivedCount(): Flow<Long>
    suspend fun insertNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(id: String)
    suspend fun togglePin(id: String)
    suspend fun archiveNote(id: String)
    suspend fun restoreNote(id: String)
}
