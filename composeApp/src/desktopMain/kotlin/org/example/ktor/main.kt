package org.example.ktor

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState

val state = WindowState(
    size = DpSize(1200.dp, 700.dp),
    position = WindowPosition(Alignment.Center)
)

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Sea Water Quality",
        state = state,
    ) {
        NifsApp()
    }
}