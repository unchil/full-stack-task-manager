package org.example.ktor

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope


@Composable
fun NifsApp() {

    MaterialTheme {
        val scope = rememberCoroutineScope()

        Text("Wonderful World!" )
    }

}