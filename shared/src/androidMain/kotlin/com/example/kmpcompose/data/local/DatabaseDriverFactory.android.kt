package com.example.kmpcompose.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.kmpcompose.database.NotesDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = NotesDatabase.Schema,
            context = context,
            name = "notes.db"
        )
    }
}
