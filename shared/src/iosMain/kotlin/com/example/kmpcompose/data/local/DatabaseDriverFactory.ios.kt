package com.example.kmpcompose.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.kmpcompose.database.NotesDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = NotesDatabase.Schema,
            name = "notes.db"
        )
    }
}
