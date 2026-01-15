package com.example.kmpcompose.presentation.mvi

sealed interface DetailsEffect {
    data object NavigateBack : DetailsEffect
    data class ShowError(val message: String) : DetailsEffect
}
