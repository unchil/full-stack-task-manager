package org.example.ktor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.example.ktor.data.DATA_DIVISION
import org.jetbrains.letsPlot.geom.geomBoxplot
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.skia.compose.PlotPanel

@Composable
fun NifsObservationBoxPlot(modifier: Modifier = Modifier) {
    MaterialTheme {

        val viewModel = remember { NifsSeaWaterInfoOneDayViewModel() }

        LaunchedEffect(key1 = viewModel){
            viewModel.onEvent(NifsSeaWaterInfoOneDayViewModel.Event.ObservationRefresh(DATA_DIVISION.oneday))

            while(true){
                // 10분(600,000 밀리초)마다 실행되는 타이머 설정
                // Long 타입으로 명시 (권장)
                delay(10 * 60 * 1000L).let{
                    viewModel.onEvent(NifsSeaWaterInfoOneDayViewModel.Event.ObservationRefresh(DATA_DIVISION.oneday))
                }
            }
        }

        var figureBoxPlot: Plot by remember { mutableStateOf(letsPlot() + geomBoxplot()) }
        val preserveAspectRatio = remember { mutableStateOf(false) }
        val seaWaterInfoOneday = viewModel._seaWaterInfoOneDayStateFlow.collectAsState()

        LaunchedEffect(key1= seaWaterInfoOneday.value){
            figureBoxPlot = createBoxPlotChart(seaWaterInfoOneday.value.toBoxPlotData())
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
