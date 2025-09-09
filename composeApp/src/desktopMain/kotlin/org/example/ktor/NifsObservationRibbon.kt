package org.example.ktor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.example.ktor.data.DATA_DIVISION
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.geom.geomRibbon
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.ggsize
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import org.jetbrains.letsPlot.themes.elementText
import org.jetbrains.letsPlot.themes.theme

@Composable
fun NifsObservationRibbon(modifier: Modifier = Modifier) {
    MaterialTheme {

        val viewModel = remember { NifsSeaWaterInfoStatViewModel() }

        LaunchedEffect(key1 = viewModel){
            viewModel.onEvent(NifsSeaWaterInfoStatViewModel.Event.ObservationStatRefresh)
            while(true){
                delay(1800 * 1000).let {
                    viewModel.onEvent(NifsSeaWaterInfoStatViewModel.Event.ObservationStatRefresh)
                }
            }
        }

        var figureLine: Plot by remember { mutableStateOf(letsPlot() + geomRibbon()) }
        val preserveAspectRatio = remember { mutableStateOf(false) }
        val seaWaterInfoStat = viewModel._seaWaterInfoStatStateFlow.collectAsState()

        LaunchedEffect(key1= seaWaterInfoStat.value){
            figureLine = createRibbonChart(seaWaterInfoStat.value.toRibbonData(SEA_AREA.GRU_NAME.EAST))
        }

        Row(modifier = Modifier.then(modifier).padding(vertical = 8.dp)) {
            PlotPanel(
                modifier = Modifier.size(width = 1300.dp, height = 400.dp),
                figure = figureLine,
                preserveAspectRatio = preserveAspectRatio.value
            ) { computationMessages ->
                computationMessages.forEach { println("[APP MESSAGE] $it") }
            }
        }

    }
}