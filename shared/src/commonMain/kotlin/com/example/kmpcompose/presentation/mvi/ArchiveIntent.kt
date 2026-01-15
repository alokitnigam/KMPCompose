package com.example.kmpcompose.presentation.mvi

sealed interface ArchiveIntent {
    data object LoadArchivedNotes : ArchiveIntent
    data class RestoreNote(val noteId: String) : ArchiveIntent
    data class DeleteNote(val noteId: String) : ArchiveIntent
}
