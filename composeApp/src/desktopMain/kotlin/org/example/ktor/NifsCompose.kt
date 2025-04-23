package org.example.ktor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NifsCompose(){

    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
    ){

        Text("Nifs Sea Water Temperature Infomation",
            modifier=Modifier.fillMaxWidth().align(Alignment.CenterHorizontally).padding(vertical = 20.dp),
            fontSize=20.sp ,
            fontWeight= FontWeight.Bold
        )

        NifsSeaWaterInfoDataGrid(modifier = Modifier.fillMaxWidth().height(400.dp ))
        NifsObservationBoxPlot(modifier = Modifier.fillMaxWidth())
        NifsObservationLayerBars(modifier = Modifier.fillMaxWidth() )
        NifsObservationLine(modifier = Modifier.fillMaxWidth() )
        NifsObservationRibbon(modifier = Modifier.fillMaxWidth() )

    }
}