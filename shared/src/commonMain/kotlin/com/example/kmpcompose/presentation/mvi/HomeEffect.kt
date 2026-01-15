package com.example.kmpcompose.presentation.mvi

sealed interface HomeEffect {
    data class NavigateToDetails(val noteId: String?) : HomeEffect
    data object NavigateToArchive : HomeEffect
    data class ShowError(val message: String) : HomeEffect
}
