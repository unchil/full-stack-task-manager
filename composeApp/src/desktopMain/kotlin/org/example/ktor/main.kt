package org.example.ktor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import org.jetbrains.letsPlot.*
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults
import org.jetbrains.letsPlot.pos.positionDodge
import org.jetbrains.letsPlot.geom.geomBar
import org.jetbrains.letsPlot.geom.geomDensity
import org.jetbrains.letsPlot.geom.geomPie
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.scale.scaleYContinuous
import org.jetbrains.letsPlot.scale.xlim
import org.jetbrains.letsPlot.scale.ylim

import org.jetbrains.letsPlot.skia.compose.PlotPanel

val state = WindowState(
    size = DpSize(1200.dp, 700.dp),
    position = WindowPosition(Alignment.Center)
)

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Sea Water Quality",
        state = state,
    ) {
        NifsDeskApp()
    }
}

@Composable
fun NifsDeskApp(){

    MaterialTheme {

        val scope = rememberCoroutineScope()
        val viewModel = remember {
            NifsViewModel(scope)
        }

        val seaWaterInfoCurrent = viewModel._seaWaterInfoCurrentStateFlow.collectAsState().value
        val preserveAspectRatio = remember { mutableStateOf(false) }

        fun blankPlot(): Plot {
            var plot = letsPlot() + geomPoint()
            plot += xlim(listOf(-3.0, 3.0)) + ylim(listOf(0.0, 0.5))
            return plot
        }

        var figure: Plot by remember { mutableStateOf(blankPlot()) }

        LaunchedEffect(key1= viewModel._seaWaterInfoCurrentStateFlow.collectAsState().value){

            val sta_nam_kor = mutableListOf<String>()
            val obs_lay = mutableListOf<String>()
            val wtr_tmp = mutableListOf<Float>()

            seaWaterInfoCurrent.filter {
                it.gru_nam.equals("동해")
            }.forEach {
                sta_nam_kor.add(it.sta_nam_kor)
                obs_lay.add(
                    when(it.obs_lay) {
                        "1" -> "표층"
                        "2" -> "중층"
                        "3" -> "저층"
                        else -> {""}
                    }
                )
                wtr_tmp.add(it.wtr_tmp.toFloat())
            }

            val data = mapOf<String, List<Any>>( "관측지점" to sta_nam_kor, "관측수심" to obs_lay, "온도" to wtr_tmp  )

            figure = letsPlot(data) { x = "관측지점" } +
                    geomBar(position = positionDodge(), alpha = 0.5) {
                        fill = "관측수심"
                        weight = "온도"
                    } +
                    labs(title = "Korea Sea Water Quality", y = "온도 °C") +
                    scaleYContinuous(limits = Pair(0, 15)) +
                    ggsize(width = 1200, height = 300)

        }


        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {

            PlotPanel(
                figure = figure,
                preserveAspectRatio = preserveAspectRatio.value,
                modifier = Modifier.fillMaxSize()
            ) { computationMessages ->
                computationMessages.forEach { println("[APP MESSAGE] $it") }
            }


/*
            LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                items(seaWaterInfoCurrent.count()) { index ->
                    Text("${seaWaterInfoCurrent[index].sta_nam_kor}:${seaWaterInfoCurrent[index].obs_lay}:${seaWaterInfoCurrent[index].wtr_tmp}:${seaWaterInfoCurrent[index].lon} :${seaWaterInfoCurrent[index].lat}    "  )
                }
            }

 */

        }

    }

}