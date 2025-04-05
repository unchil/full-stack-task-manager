package org.example.ktor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NifsCompose(){
    MaterialTheme {

        Box(
            modifier = Modifier.fillMaxSize()
                .background(color = Color(255, 255, 255))
                .padding(10.dp)
        ){

            val stateVertical = rememberScrollState(0)
            val stateHorizontal = rememberScrollState(0)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(stateVertical)
                    .padding(end = 12.dp, bottom = 12.dp)
                    .horizontalScroll(stateHorizontal)
            ) {

                Column {
                    NifsObservationBoxPlot(modifier = Modifier.fillMaxWidth())
                    NifsObservationLayerBars(modifier = Modifier.fillMaxWidth() )
                    NifsObservationLine(modifier = Modifier.fillMaxWidth() )
                    NifsObservationRibbon(modifier = Modifier.fillMaxWidth() )
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = stateVertical
                )
            )

            HorizontalScrollbar(
                modifier = Modifier.align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(end = 12.dp),
                adapter = rememberScrollbarAdapter(stateHorizontal)
            )

        }

    }
}