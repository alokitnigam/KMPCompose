package com.example.kmpcompose.di

import com.example.kmpcompose.data.local.DatabaseDriverFactory
import com.example.kmpcompose.data.repository.NoteRepository
import com.example.kmpcompose.data.repository.NoteRepositoryImpl
import com.example.kmpcompose.database.NotesDatabase
import com.example.kmpcompose.domain.usecase.*
import com.example.kmpcompose.presentation.archive.ArchiveScreenModel
import com.example.kmpcompose.presentation.details.DetailsScreenModel
import com.example.kmpcompose.presentation.home.HomeScreenModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

expect val platformModule: Module

val appModule = module {
    single { 
        NotesDatabase(get<DatabaseDriverFactory>().createDriver())
    }
    
    single<NoteRepository> { 
        NoteRepositoryImpl(get())
    }
    
    // Use cases
    factory { GetNotesUseCase(get()) }
    factory { GetPinnedNotesUseCase(get()) }
    factory { GetArchivedNotesUseCase(get()) }
    factory { GetArchivedCountUseCase(get()) }
    factory { SaveNoteUseCase(get()) }
    factory { DeleteNoteUseCase(get()) }
    factory { TogglePinNoteUseCase(get()) }
    factory { ArchiveNoteUseCase(get()) }
    factory { RestoreNoteUseCase(get()) }
    
    // Screen Models
    factoryOf(::HomeScreenModel)
    factoryOf(::ArchiveScreenModel)
    factory { (noteId: String?) -> 
        DetailsScreenModel(get(), get(), get(), noteId)
    }
}
