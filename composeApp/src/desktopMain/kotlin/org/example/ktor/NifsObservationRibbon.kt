package org.example.ktor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.example.ktor.SEA_AREA.gru_nam
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

        var selectedOption by remember { mutableStateOf(SEA_AREA.GRU_NAME.entries[0]) }

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

        LaunchedEffect(key1= seaWaterInfoStat.value, key2=selectedOption){
            figureLine = createRibbonChart( seaWaterInfoStat.value.toRibbonData(selectedOption.gru_nam()), selectedOption.name)
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
                modifier = Modifier.size(width = 1300.dp, height = 400.dp),
                figure = figureLine,
                preserveAspectRatio = preserveAspectRatio.value
            ) { computationMessages ->
                computationMessages.forEach { println("[APP MESSAGE] $it") }
            }
        }




    }
}