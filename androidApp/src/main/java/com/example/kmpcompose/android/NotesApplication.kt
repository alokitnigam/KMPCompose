package com.example.kmpcompose.android

import android.app.Application
import com.example.kmpcompose.di.initKoin

class NotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(this)
    }
}
