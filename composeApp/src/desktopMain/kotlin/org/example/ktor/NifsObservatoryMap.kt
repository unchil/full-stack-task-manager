package org.example.ktor

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.letsPlot

@Composable
fun NifsObservatoryMap(modifier:Modifier = Modifier) {

    MaterialTheme {

        val viewModel = remember { NifsSeaWaterInfoCurrentViewModel() }
        val seaWaterInfoCurrent = viewModel._seaWaterInfoCurrentStateFlow.collectAsState().value
        val preserveAspectRatio = remember { mutableStateOf(false) }
        var figure: Plot by remember { mutableStateOf(letsPlot() + geomPoint()) }





    }
}