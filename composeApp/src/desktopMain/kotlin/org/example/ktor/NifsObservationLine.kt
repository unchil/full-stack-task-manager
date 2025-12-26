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
import org.example.ktor.SEA_AREA.gru_nam
import org.example.ktor.data.DATA_DIVISION
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.skia.compose.PlotPanel

@Composable
fun NifsObservationLine(modifier: Modifier = Modifier) {
    MaterialTheme {

        val viewModel = remember { NifsSeaWaterInfoOneDayViewModel() }

        var selectedOption by remember { mutableStateOf(SEA_AREA.GRU_NAME.entries[0]) }

        LaunchedEffect(key1 = viewModel){
            viewModel.onEvent(NifsSeaWaterInfoOneDayViewModel.Event.ObservationRefresh(DATA_DIVISION.oneday))
            while(true){
                delay(10 * 60 * 1000L).let{
                    viewModel.onEvent(NifsSeaWaterInfoOneDayViewModel.Event.ObservationRefresh(DATA_DIVISION.oneday))
                }
            }
        }

        var figureLine: Plot by remember { mutableStateOf(letsPlot() + geomLine()) }
        val preserveAspectRatio = remember { mutableStateOf(false) }
        val seaWaterInfoOneday = viewModel._seaWaterInfoOneDayStateFlow.collectAsState()

        LaunchedEffect(key1= seaWaterInfoOneday.value, key2=selectedOption){
            figureLine = createLineChart( seaWaterInfoOneday.value.toLineData(selectedOption.gru_nam()) , selectedOption.name  )
        }

        Column (modifier = Modifier.then(modifier).padding(vertical = 8.dp)) {

            Row {
                SEA_AREA.GRU_NAME.entries.forEach { entrie ->
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
                            text = entrie.name,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }


            PlotPanel(
                modifier = Modifier.fillMaxWidth().height( 400.dp),
                figure = figureLine,
                preserveAspectRatio = preserveAspectRatio.value
            ) { computationMessages ->
                computationMessages.forEach { println("[APP MESSAGE] $it") }
            }
        }

    }
}