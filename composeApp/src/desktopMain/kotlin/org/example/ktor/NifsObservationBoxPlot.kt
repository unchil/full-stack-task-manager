package org.example.ktor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.ktor.data.DATA_DIVISION
import org.jetbrains.letsPlot.asDiscrete
import org.jetbrains.letsPlot.geom.geomBoxplot
import org.jetbrains.letsPlot.ggsize
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.scale.scaleColorViridis
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import org.jetbrains.letsPlot.themes.elementText
import org.jetbrains.letsPlot.themes.theme

@Composable
fun NifsObservationBoxPlot(modifier: Modifier = Modifier) {
    MaterialTheme {

        val viewModel = remember { NifsSeaWaterInfoOneDayViewModel() }

        LaunchedEffect(key1 = viewModel){
            viewModel.onEvent(NifsSeaWaterInfoOneDayViewModel.Event.ObservationRefresh(DATA_DIVISION.oneday))
        }

        var figureBoxPlot: Plot by remember { mutableStateOf(letsPlot() + geomBoxplot()) }
        val preserveAspectRatio = remember { mutableStateOf(false) }
        val seaWaterInfoOneday = viewModel._seaWaterInfoOneDayStateFlow.collectAsState().value

        LaunchedEffect(key1= seaWaterInfoOneday){
            figureBoxPlot = createBoxPlotChart(seaWaterInfoOneday.toBoxPlotData())
        }

        Row(modifier = Modifier.then(modifier).padding(vertical = 8.dp)) {
            PlotPanel(
                modifier = Modifier.size(width = 1300.dp, height = 400.dp),
                figure = figureBoxPlot,
                preserveAspectRatio = preserveAspectRatio.value
            ) { computationMessages ->
                computationMessages.forEach { println("[APP MESSAGE] $it") }
            }
        }

    }
}
