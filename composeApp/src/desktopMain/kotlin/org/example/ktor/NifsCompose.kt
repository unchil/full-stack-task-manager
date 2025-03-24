package org.example.ktor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NifsCompose(){
    MaterialTheme {

        Row(modifier = Modifier.fillMaxSize()) {

            NifsObservationLayerBars(modifier = Modifier.weight(0.5f) )

            NifsObservationBoxPlot(modifier = Modifier.weight(0.5f))


        }

    }
}