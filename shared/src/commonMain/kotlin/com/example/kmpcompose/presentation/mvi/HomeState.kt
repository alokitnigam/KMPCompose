package com.example.kmpcompose.presentation.mvi

import com.example.kmpcompose.domain.model.Note

data class HomeState(
    val pinnedNotes: List<Note> = emptyList(),
    val normalNotes: List<Note> = emptyList(),
    val archivedCount: Long = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)
