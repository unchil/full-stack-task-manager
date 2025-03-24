package org.example.ktor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NifsCompose(){
    MaterialTheme {

        Column (modifier = Modifier.fillMaxSize()) {

            Row{
                NifsObservationBoxPlot(modifier = Modifier.weight(0.5f))
                NifsObservationLayerBars(modifier = Modifier.weight(0.5f) )
            }

            NifsObservationLine(modifier = Modifier.fillMaxWidth() )
        }

    }
}