package com.example.kmpcompose

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform