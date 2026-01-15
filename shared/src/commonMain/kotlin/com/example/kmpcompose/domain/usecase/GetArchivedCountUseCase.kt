package com.example.kmpcompose.domain.usecase

import com.example.kmpcompose.data.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetArchivedCountUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<Long> = repository.getArchivedCount()
}
