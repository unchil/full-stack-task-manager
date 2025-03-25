package org.example.ktor

import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NifsCompose(){

    val scrollState = rememberScrollState()

    MaterialTheme {

        Column (modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
            Row{
                NifsObservationBoxPlot(modifier = Modifier.weight(0.5f))
                NifsObservationLayerBars(modifier = Modifier.weight(0.5f) )
            }

            NifsObservationLine(modifier = Modifier.fillMaxWidth() )

            NifsObservationRibbon(modifier = Modifier.fillMaxWidth() )
        }

    }
}