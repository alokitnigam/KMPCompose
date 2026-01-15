package com.example.kmpcompose

import androidx.compose.ui.window.ComposeUIViewController
import com.example.kmpcompose.ui.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    return ComposeUIViewController {
        App()
    }
}
