package com.example.kmpcompose.presentation.mvi

sealed interface ArchiveEffect {
    data class ShowError(val message: String) : ArchiveEffect
    data class ShowMessage(val message: String) : ArchiveEffect
}
