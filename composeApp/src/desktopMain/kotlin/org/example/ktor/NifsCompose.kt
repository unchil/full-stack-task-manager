package org.example.ktor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
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
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text("Korea Sea Water Quality Information",
            modifier=Modifier.padding( 20.dp),
            fontSize=20.sp ,
            fontWeight= FontWeight.Bold
        )

        NifsSeaWaterInfoDataGrid(modifier = Modifier.fillMaxWidth(0.9f).height(700.dp ).padding(20.dp))
    //    MofObservationLine(modifier = Modifier.fillMaxWidth(0.9f).padding(20.dp) )
        NifsObservationBoxPlot(modifier = Modifier.fillMaxWidth(0.9f).padding( 20.dp))
        NifsObservationLayerBars(modifier = Modifier.fillMaxWidth(0.9f).padding(20.dp) )
        NifsObservationLine(modifier = Modifier.fillMaxWidth(0.9f).padding(20.dp) )
        NifsObservationRibbon(modifier = Modifier.fillMaxWidth(0.9f).padding( 20.dp) )

    }
}