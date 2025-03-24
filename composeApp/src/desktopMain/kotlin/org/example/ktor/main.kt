package org.example.ktor

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application


val state = WindowState(
    size = DpSize(1400.dp, 800.dp),
    position = WindowPosition(Alignment.Center)
)

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "NIFS SeaWater Infomation",
        state = state,
    ) {
        NifsCompose()
    }
}


