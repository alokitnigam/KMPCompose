package com.example.kmpcompose.presentation.mvi

sealed interface DetailsIntent {
    data class LoadNote(val noteId: String?) : DetailsIntent
    data class UpdateTitle(val title: String) : DetailsIntent
    data class UpdateContent(val content: String) : DetailsIntent
    data object SaveNote : DetailsIntent
    data object ArchiveNote : DetailsIntent
    data object TogglePin : DetailsIntent
}
