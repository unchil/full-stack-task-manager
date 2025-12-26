package org.example.ktor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.example.ktor.data.DATA_DIVISION
import org.jetbrains.letsPlot.geom.geomBar
import org.jetbrains.letsPlot.ggsize
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.pos.positionDodge
import org.jetbrains.letsPlot.scale.scaleYContinuous
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import org.jetbrains.letsPlot.themes.elementText
import org.jetbrains.letsPlot.themes.theme
import org.jetbrains.letsPlot.tooltips.layerTooltips


@Composable
fun NifsObservationLayerBars(modifier:Modifier = Modifier) {
    MaterialTheme {

        val viewModel = remember { NifsSeaWaterInfoCurrentViewModel() }

        LaunchedEffect(key1 = viewModel){
            viewModel.onEvent(NifsSeaWaterInfoCurrentViewModel.Event.ObservationRefresh(DATA_DIVISION.current))
            while(true){
                delay(10 * 60 * 1000L).let{
                    viewModel.onEvent(NifsSeaWaterInfoCurrentViewModel.Event.ObservationRefresh(DATA_DIVISION.current))
                }
            }
        }

        val preserveAspectRatio = remember { mutableStateOf(false) }
        var figure: Plot by remember { mutableStateOf(letsPlot() + geomBar()) }
        val seaWaterInfoCurrent = viewModel._seaWaterInfoCurrentStateFlow.collectAsState()

        LaunchedEffect(key1= seaWaterInfoCurrent.value){
            figure = createBarChart(seaWaterInfoCurrent.value.toLayerBarsData())
        }

        Row(modifier = Modifier.then(modifier).padding(vertical = 8.dp)) {
            PlotPanel(
                modifier = Modifier.fillMaxWidth().height( 400.dp),
                figure = figure,
                preserveAspectRatio = preserveAspectRatio.value
            ) { computationMessages ->
                computationMessages.forEach { println("[APP MESSAGE] $it") }
            }
        }

    }
}

