package com.example.kmpcompose.presentation.mvi

import com.example.kmpcompose.domain.model.Note

data class ArchiveState(
    val archivedNotes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
