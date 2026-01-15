package com.example.kmpcompose.presentation.mvi

sealed interface HomeIntent {
    data object LoadNotes : HomeIntent
    data object AddNote : HomeIntent
    data class EditNote(val noteId: String) : HomeIntent
    data class DeleteNote(val noteId: String) : HomeIntent
    data class TogglePin(val noteId: String) : HomeIntent
    data class ArchiveNote(val noteId: String) : HomeIntent
    data object NavigateToArchive : HomeIntent
}
