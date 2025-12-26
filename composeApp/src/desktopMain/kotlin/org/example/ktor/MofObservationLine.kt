package org.example.ktor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.example.ktor.WATER_QUALITY.name
import org.example.ktor.data.DATA_DIVISION
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.skia.compose.PlotPanel

@Composable
fun MofObservationLine(modifier: Modifier = Modifier)  {
    MaterialTheme {

        val viewModel = remember {
            MofSeaWaterInfoOneDayViewModel()
        }

        var selectedOption by remember { mutableStateOf(WATER_QUALITY.QualityType.entries[0]) }

        LaunchedEffect(key1 = viewModel){
            viewModel.onEvent(MofSeaWaterInfoOneDayViewModel.Event.ObservationRefresh(DATA_DIVISION.mof_oneday))
            while(true){
                delay(10 * 60 * 1000L).let{
                    viewModel.onEvent(MofSeaWaterInfoOneDayViewModel.Event.ObservationRefresh(DATA_DIVISION.mof_oneday))
                }
            }
        }

        var figureLine: Plot by  remember{ mutableStateOf(letsPlot() + geomLine()) }
        val preserveAspectRatio = remember { mutableStateOf(false) }
        val seaWaterInfoOneday = viewModel._seaWaterInfoOneDayStateFlow.collectAsState()


        LaunchedEffect(key1= seaWaterInfoOneday.value, key2= selectedOption.name){
            figureLine = createLineChart2( seaWaterInfoOneday.value.toMofLineData(selectedOption) , selectedOption )
        }

        Column (modifier = Modifier.then(modifier).padding(vertical = 8.dp)) {

            Row {
                WATER_QUALITY.QualityType.entries.forEach { entrie ->
                    Row(
                        Modifier
                            .selectable(
                                selected = (entrie == selectedOption),
                                onClick = { selectedOption = entrie }
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (entrie == selectedOption),
                            onClick = { selectedOption = entrie }
                        )
                        Text(
                            text = entrie.name(),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }

            PlotPanel(
                modifier = Modifier.fillMaxWidth().height(700.dp),
                figure = figureLine,
                preserveAspectRatio = preserveAspectRatio.value
            ) { computationMessages ->
                computationMessages.forEach { println("[APP MESSAGE] $it") }
            }
        }


    }
}