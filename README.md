# KMP Notes App - Architecture & Technical Documentation

## Table of Contents
- [Overview](#overview)
- [Architecture Pattern](#architecture-pattern)
- [Project Structure](#project-structure)
- [Core Technologies](#core-technologies)
- [Libraries & Dependencies](#libraries--dependencies)
- [Data Flow](#data-flow)
- [Platform-Specific Implementations](#platform-specific-implementations)
- [Dependency Injection](#dependency-injection)
- [Navigation](#navigation)
- [Database Schema](#database-schema)

---

## Overview

This is a **Kotlin Multiplatform (KMP)** notes application with **100% shared UI** built using **Compose Multiplatform**. The app demonstrates modern Android/iOS development with a clean, maintainable architecture.

### Key Features
- ✅ Create, edit, and delete notes
- ✅ Pin important notes
- ✅ Archive/restore notes
- ✅ Local SQLite persistence
- ✅ Shared UI across Android & iOS
- ✅ MVI architecture pattern
- ✅ Reactive data flow with Kotlin Flows

### Tech Stack Summary
- **Language:** Kotlin 2.0.0
- **UI Framework:** Compose Multiplatform 1.6.11
- **Architecture:** MVI (Model-View-Intent)
- **Database:** SQLDelight 2.0.2
- **Navigation:** Voyager 1.1.0-beta02
- **DI:** Koin 3.5.6
- **Async:** Kotlinx Coroutines 1.8.1

---

## Architecture Pattern

### MVI (Model-View-Intent)

The app follows the **MVI architecture pattern** for unidirectional data flow and predictable state management.

```
┌──────────┐
│   View   │ ◄────────────────┐
└──────────┘                  │
     │                        │
     │ (User Actions)         │ (State Updates)
     ▼                        │
┌──────────┐             ┌─────────┐
│  Intent  │ ───────────►│  Model  │
└──────────┘             └─────────┘
                              │
                              │ (Side Effects)
                              ▼
                         ┌─────────┐
                         │ Effect  │
                         └─────────┘
```

#### Components:

**1. State** - Immutable UI state
```kotlin
data class HomeState(
    val pinnedNotes: List<Note> = emptyList(),
    val normalNotes: List<Note> = emptyList(),
    val archivedCount: Int = 0,
    val isLoading: Boolean = false
)
```

**2. Intent** - User actions
```kotlin
sealed class HomeIntent {
    data object LoadNotes : HomeIntent()
    data class DeleteNote(val noteId: String) : HomeIntent()
    data class TogglePin(val noteId: String) : HomeIntent()
    data class ArchiveNote(val noteId: String) : HomeIntent()
}
```

**3. Effect** - One-time side effects
```kotlin
sealed class HomeEffect {
    data class NavigateToDetails(val noteId: String?) : HomeEffect()
    data class ShowError(val message: String) : HomeEffect()
}
```

### Layered Architecture

The app follows **Clean Architecture** principles with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (ScreenModels, State, Intent, Effect)  │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│           Domain Layer                  │
│    (Use Cases, Business Logic, Models)  │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│            Data Layer                   │
│   (Repositories, Database, Drivers)     │
└─────────────────────────────────────────┘
```

**Layers Explained:**

1. **Presentation Layer** (`ui/`, `presentation/`)
   - Compose UI screens and components
   - Voyager ScreenModels for state management
   - MVI state/intent/effect classes
   - Zero business logic

2. **Domain Layer** (`domain/`)
   - Use Cases (single responsibility)
   - Domain models (Note data class)
   - Business rules and validation
   - Platform-agnostic

3. **Data Layer** (`data/`)
   - Repository implementations
   - Database queries (SQLDelight)
   - Platform-specific drivers
   - Data source abstraction

---

## Project Structure

```
KMPCompose/
├── androidApp/                    # Android application module
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/example/kmpcompose/
│   │   │   ├── MainActivity.kt    # Android entry point
│   │   │   └── NotesApplication.kt # Koin initialization
│   └── build.gradle.kts
│
├── iosApp/                        # iOS application
│   ├── iosApp/
│   │   ├── iOSApp.swift           # iOS entry point
│   │   ├── ContentView.swift      # SwiftUI wrapper
│   │   └── Info.plist
│   └── iosApp.xcodeproj/
│
├── shared/                        # Shared KMP module
│   ├── src/
│   │   ├── commonMain/            # Cross-platform code
│   │   │   ├── kotlin/
│   │   │   │   ├── ui/
│   │   │   │   │   ├── App.kt                    # Root Composable
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── HomeScreen.kt         # Main notes list
│   │   │   │   │   │   ├── DetailsScreen.kt      # Note editor
│   │   │   │   │   │   └── ArchiveScreen.kt      # Archived notes
│   │   │   │   │   └── components/
│   │   │   │   │       └── NoteCard.kt           # Reusable note card
│   │   │   │   │
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── home/
│   │   │   │   │   │   └── HomeScreenModel.kt    # Home logic
│   │   │   │   │   ├── details/
│   │   │   │   │   │   └── DetailsScreenModel.kt # Editor logic
│   │   │   │   │   ├── archive/
│   │   │   │   │   │   └── ArchiveScreenModel.kt # Archive logic
│   │   │   │   │   └── mvi/
│   │   │   │   │       ├── NoteState.kt          # State classes
│   │   │   │   │       ├── NoteIntent.kt         # Intent classes
│   │   │   │   │       └── NoteEffect.kt         # Effect classes
│   │   │   │   │
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/
│   │   │   │   │   │   └── Note.kt               # Domain model
│   │   │   │   │   └── usecase/
│   │   │   │   │       ├── GetNotesUseCase.kt
│   │   │   │   │       ├── GetPinnedNotesUseCase.kt
│   │   │   │   │       ├── GetArchivedNotesUseCase.kt
│   │   │   │   │       ├── GetArchivedCountUseCase.kt
│   │   │   │   │       ├── SaveNoteUseCase.kt
│   │   │   │   │       ├── DeleteNoteUseCase.kt
│   │   │   │   │       ├── TogglePinUseCase.kt
│   │   │   │   │       ├── ArchiveNoteUseCase.kt
│   │   │   │   │       └── RestoreNoteUseCase.kt
│   │   │   │   │
│   │   │   │   ├── data/
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── NoteRepository.kt     # Interface
│   │   │   │   │   │   └── NoteRepositoryImpl.kt # Implementation
│   │   │   │   │   └── local/
│   │   │   │   │       └── DatabaseDriverFactory.kt # expect class
│   │   │   │   │
│   │   │   │   ├── di/
│   │   │   │   │   └── AppModule.kt              # Koin common module
│   │   │   │   │
│   │   │   │   └── util/
│   │   │   │       └── TimeUtil.kt               # expect fun
│   │   │   │
│   │   │   └── sqldelight/
│   │   │       └── com/example/kmpcompose/database/
│   │   │           └── Note.sq                   # Database schema
│   │   │
│   │   ├── androidMain/           # Android-specific code
│   │   │   └── kotlin/
│   │   │       ├── data/local/
│   │   │       │   └── DatabaseDriverFactory.android.kt
│   │   │       ├── di/
│   │   │       │   └── PlatformModule.android.kt
│   │   │       └── util/
│   │   │           └── TimeUtil.android.kt
│   │   │
│   │   └── iosMain/               # iOS-specific code
│   │       └── kotlin/
│   │           ├── data/local/
│   │           │   └── DatabaseDriverFactory.ios.kt
│   │           ├── di/
│   │           │   └── PlatformModule.ios.kt
│   │           ├── MainViewController.kt
│   │           └── util/
│   │               └── TimeUtil.ios.kt
│   │
│   └── build.gradle.kts
│
├── gradle/
│   └── libs.versions.toml         # Version catalog
├── build.gradle.kts
└── settings.gradle.kts
```

---

## Core Technologies

### 1. Kotlin Multiplatform (KMP)

**Version:** 2.0.0

**Purpose:** Share code across Android, iOS, Desktop, and Web platforms.

**Benefits:**
- Single codebase for business logic and UI
- Type-safe code sharing
- Platform-specific implementations when needed (expect/actual)
- Native performance on all platforms

**Configuration:**
```kotlin
kotlin {
    androidTarget { ... }           // Android JVM target
    iosX64()                        // iOS Intel simulator
    iosArm64()                      // iOS physical devices
    iosSimulatorArm64()             // iOS M1/M2 simulator
}
```

### 2. Compose Multiplatform

**Version:** 1.6.11

**Purpose:** Declarative UI framework for building native UIs across platforms.

**Key Features:**
- Single UI codebase for Android & iOS
- Reactive UI updates
- Material 3 design system
- Rich set of components and icons

**Example:**
```kotlin
@Composable
fun HomeScreen() {
    LazyColumn {
        items(notes) { note ->
            NoteCard(
                note = note,
                onDelete = { /* ... */ }
            )
        }
    }
}
```

### 3. Gradle Build System

**Version:** 8.11.1

**Features:**
- Version catalogs for dependency management
- Kotlin DSL for type-safe build scripts
- Incremental compilation
- Configuration caching

**Version Catalog:** (`gradle/libs.versions.toml`)
```toml
[versions]
kotlin = "2.0.0"
compose = "1.6.11"
sqldelight = "2.0.2"

[libraries]
voyager-navigator = { module = "cafe.adriel.voyager:voyager-navigator", version.ref = "voyager" }
```

---

## Libraries & Dependencies

### UI & Navigation

#### **Compose Multiplatform** (1.6.11)
- `compose.runtime` - Reactive runtime
- `compose.foundation` - Basic building blocks
- `compose.material3` - Material Design 3 components
- `compose.materialIconsExtended` - Extended icon set (2000+ icons)
- `compose.ui` - Core UI primitives

**Usage:**
```kotlin
@Composable
fun NoteCard(note: Note) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(note.title, style = MaterialTheme.typography.titleMedium)
        Text(note.content, style = MaterialTheme.typography.bodyMedium)
    }
}
```

#### **Voyager** (1.1.0-beta02)
- Navigation library for Compose Multiplatform
- Type-safe navigation
- ScreenModel lifecycle management
- Tab navigation support
- Smooth transitions

**Modules:**
- `voyager-navigator` - Core navigation
- `voyager-screenmodel` - Lifecycle-aware ViewModels
- `voyager-tab-navigator` - Bottom navigation
- `voyager-transitions` - Screen transitions
- `voyager-koin` - Koin integration for DI

**Usage:**
```kotlin
// Screen definition
class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<HomeScreenModel>()
        // UI code
    }
}

// Navigation
navigator.push(DetailsScreen(noteId))
navigator.pop()
```

### Database

#### **SQLDelight** (2.0.2)
- Type-safe SQL database library
- Generates Kotlin code from SQL
- Supports Android (SQLite) and iOS (native driver)
- Reactive queries with Kotlin Flow

**Modules:**
- `sqldelight.runtime` - Core runtime
- `sqldelight.coroutines` - Flow support
- `sqldelight.android.driver` - Android driver
- `sqldelight.native.driver` - iOS driver

**Schema (`Note.sq`):**
```sql
CREATE TABLE Note (
    id TEXT PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    isPinned INTEGER AS Boolean NOT NULL DEFAULT 0,
    isArchived INTEGER AS Boolean NOT NULL DEFAULT 0,
    createdAt INTEGER NOT NULL,
    updatedAt INTEGER NOT NULL
);

selectAllNotes:
SELECT * FROM Note
WHERE isArchived = 0
ORDER BY isPinned DESC, updatedAt DESC;
```

**Generated Kotlin API:**
```kotlin
database.noteQueries.selectAllNotes().asFlow().mapToList()
```

### Dependency Injection

#### **Koin** (3.5.6)
- Lightweight DI framework for Kotlin
- No code generation
- DSL-based configuration
- Multiplatform support

**Modules:**
- `koin-core` - Core DI (common)
- `koin-android` - Android extensions

**Configuration:**
```kotlin
val appModule = module {
    // Database
    single { NotesDatabase(get<SqlDriver>()) }
    
    // Repository
    single<NoteRepository> { NoteRepositoryImpl(get()) }
    
    // Use Cases
    factory { GetNotesUseCase(get()) }
    factory { SaveNoteUseCase(get()) }
    
    // ScreenModels
    factory { HomeScreenModel(get(), get(), get(), get()) }
    factory { (noteId: String?) -> DetailsScreenModel(noteId, get(), get()) }
}
```

### Utilities

#### **Kotlinx Libraries**
- `kotlinx-coroutines-core` (1.8.1) - Async/await, Flow
- `kotlinx-serialization-json` (1.6.3) - JSON serialization
- `kotlinx-uuid` (0.0.26) - UUID generation
- `kotlinx-datetime` (0.5.0) - Multiplatform date/time

**Usage:**
```kotlin
// Coroutines
viewModelScope.launch {
    notes.collect { data ->
        _state.update { it.copy(notes = data) }
    }
}

// DateTime
val instant = Instant.fromEpochMilliseconds(timestamp)
val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault())

// UUID
val noteId = uuid4().toString()
```

---

## Data Flow

### Reactive Data Architecture

```
┌─────────────┐
│  Database   │
│  (SQLite)   │
└──────┬──────┘
       │
       │ Flow<List<Note>>
       ▼
┌─────────────┐
│ Repository  │
│   (Impl)    │
└──────┬──────┘
       │
       │ Flow<List<Note>>
       ▼
┌─────────────┐
│  Use Case   │
└──────┬──────┘
       │
       │ Flow<List<Note>>
       ▼
┌─────────────┐
│ ScreenModel │
│   (MVI)     │
└──────┬──────┘
       │
       │ StateFlow<HomeState>
       ▼
┌─────────────┐
│   Screen    │
│ (Compose UI)│
└─────────────┘
```

### Example Flow: Loading Notes

**1. User Action (Intent)**
```kotlin
// UI sends intent
screenModel.processIntent(HomeIntent.LoadNotes)
```

**2. ScreenModel Processes Intent**
```kotlin
fun processIntent(intent: HomeIntent) {
    when (intent) {
        is HomeIntent.LoadNotes -> loadNotes()
    }
}

private fun loadNotes() {
    screenModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        
        combine(
            getNotesUseCase(),
            getPinnedNotesUseCase(),
            getArchivedCountUseCase()
        ) { notes, pinned, count ->
            Triple(notes, pinned, count)
        }.collect { (notes, pinned, count) ->
            _state.update {
                it.copy(
                    pinnedNotes = pinned,
                    normalNotes = notes.filter { !it.isPinned && !it.isArchived },
                    archivedCount = count,
                    isLoading = false
                )
            }
        }
    }
}
```

**3. Use Case Fetches Data**
```kotlin
class GetNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> = repository.getAllNotes()
}
```

**4. Repository Queries Database**
```kotlin
class NoteRepositoryImpl(database: NotesDatabase) : NoteRepository {
    private val queries = database.noteQueries
    
    override fun getAllNotes(): Flow<List<Note>> {
        return queries.selectAllNotes()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { dbNotes -> dbNotes.map { it.toNote() } }
    }
}
```

**5. UI Observes State**
```kotlin
@Composable
fun HomeScreen.Content() {
    val state by screenModel.state.collectAsState()
    
    if (state.isLoading) {
        CircularProgressIndicator()
    } else {
        LazyColumn {
            items(state.pinnedNotes) { note ->
                NoteCard(note)
            }
        }
    }
}
```

---

## Platform-Specific Implementations

### Expect/Actual Pattern

**Purpose:** Write platform-specific code while keeping the API consistent.

### Example 1: Database Driver

**Common Declaration:**
```kotlin
// shared/src/commonMain/kotlin/data/local/DatabaseDriverFactory.kt
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
```

**Android Implementation:**
```kotlin
// shared/src/androidMain/kotlin/data/local/DatabaseDriverFactory.android.kt
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = NotesDatabase.Schema,
            context = context,
            name = "notes.db"
        )
    }
}
```

**iOS Implementation:**
```kotlin
// shared/src/iosMain/kotlin/data/local/DatabaseDriverFactory.ios.kt
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = NotesDatabase.Schema,
            name = "notes.db"
        )
    }
}
```

### Example 2: Current Time

**Common Declaration:**
```kotlin
// shared/src/commonMain/kotlin/util/TimeUtil.kt
expect fun currentTimeMillis(): Long
```

**Android Implementation:**
```kotlin
// shared/src/androidMain/kotlin/util/TimeUtil.android.kt
actual fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
}
```

**iOS Implementation:**
```kotlin
// shared/src/iosMain/kotlin/util/TimeUtil.ios.kt
import platform.Foundation.NSDate

actual fun currentTimeMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}
```

---

## Dependency Injection

### Koin Module Structure

**Common Module** (`AppModule.kt`)
```kotlin
val appModule = module {
    // Database
    single {
        NotesDatabase(get<SqlDriver>())
    }
    
    // Repository
    single<NoteRepository> {
        NoteRepositoryImpl(get())
    }
    
    // Use Cases
    factory { GetNotesUseCase(get()) }
    factory { GetPinnedNotesUseCase(get()) }
    factory { GetArchivedNotesUseCase(get()) }
    factory { GetArchivedCountUseCase(get()) }
    factory { SaveNoteUseCase(get()) }
    factory { DeleteNoteUseCase(get()) }
    factory { TogglePinUseCase(get()) }
    factory { ArchiveNoteUseCase(get()) }
    factory { RestoreNoteUseCase(get()) }
    
    // ScreenModels
    factory {
        HomeScreenModel(
            getNotesUseCase = get(),
            getPinnedNotesUseCase = get(),
            getArchivedCountUseCase = get(),
            deleteNoteUseCase = get(),
            togglePinUseCase = get(),
            archiveNoteUseCase = get()
        )
    }
    
    factory { (noteId: String?) ->
        DetailsScreenModel(
            noteId = noteId,
            getNotesUseCase = get(),
            saveNoteUseCase = get()
        )
    }
    
    factory {
        ArchiveScreenModel(
            getArchivedNotesUseCase = get(),
            restoreNoteUseCase = get(),
            deleteNoteUseCase = get()
        )
    }
}
```

**Android Platform Module**
```kotlin
// shared/src/androidMain/kotlin/di/PlatformModule.android.kt
actual val platformModule: Module = module {
    // Context is provided during initialization
}

fun initKoin(context: Context) {
    startKoin {
        modules(
            module { single<Context> { context } },  // Inject Context
            platformModule,
            appModule
        )
    }
}
```

**iOS Platform Module**
```kotlin
// shared/src/iosMain/kotlin/di/PlatformModule.ios.kt
actual val platformModule: Module = module {
    single { DatabaseDriverFactory() }
}

fun initKoin() {
    startKoin {
        modules(platformModule, appModule)
    }
}
```

### Initialization

**Android:**
```kotlin
class NotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(this)  // Pass Context
    }
}
```

**iOS:**
```swift
@main
struct iOSApp: App {
    init() {
        PlatformModule_iosKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

---

## Navigation

### Voyager Navigation System

**Screen Definition:**
```kotlin
class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<HomeScreenModel>()
        
        // UI code
        FloatingActionButton(
            onClick = {
                screenModel.processIntent(
                    HomeIntent.NavigateToDetails(null)
                )
            }
        )
        
        // Effect handling
        LaunchedEffect(Unit) {
            screenModel.effect.collect { effect ->
                when (effect) {
                    is HomeEffect.NavigateToDetails ->
                        navigator.push(DetailsScreen(effect.noteId))
                }
            }
        }
    }
}
```

**Navigation Actions:**
```kotlin
// Push screen
navigator.push(DetailsScreen(noteId = "123"))

// Pop screen
navigator.pop()

// Replace screen
navigator.replace(HomeScreen())

// Pop to root
navigator.popUntilRoot()
```

**ScreenModel with Koin:**
```kotlin
@Composable
fun Screen.Content() {
    // Simple injection
    val screenModel = getScreenModel<HomeScreenModel>()
    
    // With parameters
    val screenModel = getScreenModel<DetailsScreenModel> {
        parametersOf(noteId)
    }
}
```

---

## Database Schema

### SQLDelight Schema (`Note.sq`)

```sql
-- Table definition
CREATE TABLE Note (
    id TEXT PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    isPinned INTEGER AS Boolean NOT NULL DEFAULT 0,
    isArchived INTEGER AS Boolean NOT NULL DEFAULT 0,
    createdAt INTEGER NOT NULL,
    updatedAt INTEGER NOT NULL
);

-- Create
insertNote:
INSERT INTO Note(id, title, content, isPinned, isArchived, createdAt, updatedAt)
VALUES (?, ?, ?, ?, ?, ?, ?);

-- Read
selectAllNotes:
SELECT * FROM Note
WHERE isArchived = 0
ORDER BY isPinned DESC, updatedAt DESC;

selectPinnedNotes:
SELECT * FROM Note
WHERE isPinned = 1 AND isArchived = 0
ORDER BY updatedAt DESC;

selectArchivedNotes:
SELECT * FROM Note
WHERE isArchived = 1
ORDER BY updatedAt DESC;

selectNoteById:
SELECT * FROM Note WHERE id = ?;

countArchivedNotes:
SELECT COUNT(*) FROM Note WHERE isArchived = 1;

-- Update
updateNote:
UPDATE Note
SET title = ?, content = ?, updatedAt = ?
WHERE id = ?;

togglePin:
UPDATE Note
SET isPinned = CASE WHEN isPinned = 1 THEN 0 ELSE 1 END
WHERE id = ?;

archiveNote:
UPDATE Note
SET isArchived = 1
WHERE id = ?;

restoreNote:
UPDATE Note
SET isArchived = 0
WHERE id = ?;

-- Delete
deleteNote:
DELETE FROM Note WHERE id = ?;
```

### Generated Kotlin API

SQLDelight automatically generates type-safe Kotlin code:

```kotlin
// Insert
noteQueries.insertNote(
    id = uuid4().toString(),
    title = "My Note",
    content = "Content",
    isPinned = false,
    isArchived = false,
    createdAt = currentTimeMillis(),
    updatedAt = currentTimeMillis()
)

// Query with Flow
noteQueries.selectAllNotes()
    .asFlow()
    .mapToList(Dispatchers.Default)
    .collect { notes ->
        // Handle notes list
    }

// Update
noteQueries.togglePin(noteId)

// Delete
noteQueries.deleteNote(noteId)
```

---

## Build Configuration

### Version Catalog (`gradle/libs.versions.toml`)

```toml
[versions]
kotlin = "2.0.0"
agp = "8.9.2"
compose = "1.6.11"
sqldelight = "2.0.2"
voyager = "1.1.0-beta02"
koin = "3.5.6"
kotlinx-coroutines = "1.8.1"
kotlinx-serialization = "1.6.3"
kotlinx-uuid = "0.0.26"
kotlinx-datetime = "0.5.0"

[libraries]
voyager-navigator = { module = "cafe.adriel.voyager:voyager-navigator", version.ref = "voyager" }
voyager-screenmodel = { module = "cafe.adriel.voyager:voyager-screenmodel", version.ref = "voyager" }
voyager-tab-navigator = { module = "cafe.adriel.voyager:voyager-tab-navigator", version.ref = "voyager" }
voyager-transitions = { module = "cafe.adriel.voyager:voyager-transitions", version.ref = "voyager" }
voyager-koin = { module = "cafe.adriel.voyager:voyager-koin", version.ref = "voyager" }

koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-android = { module = "io.insert-koin:koin-android", version.ref = "koin" }

sqldelight-runtime = { module = "app.cash.sqldelight:runtime", version.ref = "sqldelight" }
sqldelight-coroutines = { module = "app.cash.sqldelight:coroutines-extensions", version.ref = "sqldelight" }
sqldelight-android-driver = { module = "app.cash.sqldelight:android-driver", version.ref = "sqldelight" }
sqldelight-native-driver = { module = "app.cash.sqldelight:native-driver", version.ref = "sqldelight" }

kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "kotlinx-coroutines" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinx-serialization" }
kotlinx-uuid = { module = "app.softwork:kotlinx-uuid-core", version.ref = "kotlinx-uuid" }
kotlinx-datetime = { module = "org.jetbrains.kotlinx:kotlinx-datetime", version.ref = "kotlinx-datetime" }

[plugins]
kotlinMultiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
androidLibrary = { id = "com.android.library", version.ref = "agp" }
androidApplication = { id = "com.android.application", version.ref = "agp" }
jetbrainsCompose = { id = "org.jetbrains.compose", version.ref = "compose" }
compose-compiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlinx-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
sqldelight = { id = "app.cash.sqldelight", version.ref = "sqldelight" }
```

---

## Key Design Decisions

### 1. **MVI over MVVM**
- **Reason:** Unidirectional data flow is easier to debug and test
- **Benefit:** Predictable state changes, time-travel debugging possible

### 2. **Voyager over Official Navigation**
- **Reason:** Better Compose Multiplatform support, simpler API
- **Benefit:** Type-safe navigation, integrated ScreenModel lifecycle

### 3. **SQLDelight over Room**
- **Reason:** Room doesn't support iOS, SQLDelight is KMP-native
- **Benefit:** Shared database schema, type-safe queries, reactive Flow

### 4. **Koin over Dagger/Hilt**
- **Reason:** Lightweight, KMP-compatible, no code generation
- **Benefit:** Simple DSL, runtime DI, easy testing

### 5. **Use Cases Layer**
- **Reason:** Single Responsibility Principle
- **Benefit:** Reusable business logic, easy to test, clear separation

### 6. **100% Shared UI**
- **Reason:** Maximize code reuse
- **Benefit:** Single source of truth for UI, faster development

---

## Testing Strategy

### Unit Tests (Domain Layer)
```kotlin
class SaveNoteUseCaseTest {
    @Test
    fun `invoke should save note with generated UUID`() = runTest {
        val repository = FakeNoteRepository()
        val useCase = SaveNoteUseCase(repository)
        
        val note = Note(
            id = "",
            title = "Test",
            content = "Content"
        )
        
        useCase(note)
        
        val saved = repository.savedNotes.first()
        assertTrue(saved.id.isNotEmpty())
    }
}
```

### Integration Tests (Repository Layer)
```kotlin
class NoteRepositoryTest {
    @Test
    fun `getAllNotes should return all non-archived notes`() = runTest {
        val repository = NoteRepositoryImpl(testDatabase)
        
        repository.save(note1)
        repository.save(note2.copy(isArchived = true))
        
        val notes = repository.getAllNotes().first()
        assertEquals(1, notes.size)
    }
}
```

### UI Tests (Screen Layer)
```kotlin
@Test
fun `HomeScreen should display pinned notes first`() {
    composeTestRule.setContent {
        HomeScreen()
    }
    
    composeTestRule
        .onNodeWithTag("pinned_section")
        .assertExists()
}
```

---

## Performance Optimizations

### 1. **Lazy Lists**
```kotlin
LazyColumn {
    items(notes, key = { it.id }) { note ->
        NoteCard(note)
    }
}
```

### 2. **Flow Optimizations**
```kotlin
noteQueries.selectAllNotes()
    .asFlow()
    .mapToList(Dispatchers.Default)  // Off main thread
    .flowOn(Dispatchers.IO)
```

### 3. **State Updates**
```kotlin
_state.update { it.copy(isLoading = true) }  // Immutable update
```

### 4. **Coroutine Scoping**
```kotlin
screenModelScope.launch {
    // Automatically cancelled when ScreenModel destroyed
}
```

---

## Future Enhancements

### Planned Features
- [ ] Cloud sync (Firebase/Supabase)
- [ ] Rich text editing
- [ ] Note categories/tags
- [ ] Search functionality
- [ ] Dark mode toggle
- [ ] Note sharing
- [ ] Reminder/notifications
- [ ] Biometric lock

### Technical Improvements
- [ ] Unit test coverage >80%
- [ ] UI tests with Screenshot testing
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Crashlytics integration
- [ ] Analytics (privacy-focused)
- [ ] Offline-first sync
- [ ] Migration tests for database

---

## Conclusion

This KMP Notes app demonstrates modern mobile development best practices:

✅ **Clean Architecture** - Clear separation of concerns  
✅ **MVI Pattern** - Predictable state management  
✅ **Reactive Programming** - Kotlin Flow for data streams  
✅ **Type Safety** - Compile-time error checking  
✅ **Code Sharing** - 95%+ shared code between platforms  
✅ **Modern Libraries** - Battle-tested, community-supported  
✅ **Maintainability** - Easy to extend and refactor  

The architecture scales well for larger apps while remaining simple enough for small projects. All layers are testable, and the unidirectional data flow makes debugging straightforward.

---

## References

- [Kotlin Multiplatform Docs](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Voyager Documentation](https://voyager.adriel.cafe/)
- [SQLDelight Documentation](https://cashapp.github.io/sqldelight/)
- [Koin Documentation](https://insert-koin.io/)
- [MVI Pattern Guide](https://www.raywenderlich.com/817602-mvi-architecture-for-android-tutorial-getting-started)

---

**Last Updated:** January 14, 2026  
**App Version:** 1.0.0  
**Min SDK:** Android 24, iOS 16.0
