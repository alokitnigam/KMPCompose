package com.example.kmpcompose.presentation.mvi

data class DetailsState(
    val noteId: String? = null,
    val title: String = "",
    val content: String = "",
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isSaving: Boolean = false
)
