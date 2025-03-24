package org.example.ktor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.ggsize
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.scale.scaleYContinuous
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import org.jetbrains.letsPlot.themes.elementText
import org.jetbrains.letsPlot.themes.theme

@Composable
fun NifsObservationLine(modifier: Modifier = Modifier) {
    MaterialTheme {

        val viewModel = remember { NifsBoxPlotViewModel() }
        val seaWaterInfoOneday = viewModel._seaWaterInfoOneDayStateFlow.collectAsState().value.filter {
            it.gru_nam.equals("동해") and it.obs_lay.equals("1")
        }
        LaunchedEffect(key1 = viewModel){
            viewModel.onEvent(NifsBoxPlotViewModel.Event.ObservationRefresh("oneday"))
        }

        var figureLine: Plot by remember { mutableStateOf(letsPlot() + geomLine()) }

        val preserveAspectRatio = remember { mutableStateOf(false) }

        fun makeData():Map<String,List<Any>> {

            val sta_nam_kor = mutableListOf<String>()
            val wtr_tmp = mutableListOf<Float>()
            val obs_datetime = mutableListOf<String>()


            seaWaterInfoOneday.forEach {
                sta_nam_kor.add(it.sta_nam_kor)
                obs_datetime.add(it.obs_datetime)
                wtr_tmp.add( it.wtr_tmp.trim().toFloat()  )
            }
            return mapOf<String, List<Any>> (
                "CollectingTime" to obs_datetime,
                "ObservatoryName" to sta_nam_kor,
                "Temperature" to wtr_tmp  )
        }

        fun makeLineFigure(data:Map<String,List<Any>>): Plot {
            return letsPlot(data) +
                    geomLine { x="CollectingTime"; y="Temperature"; color="ObservatoryName"} +
                    labs( title="Korea Sea Water Quality", y="수온 °C", x="관측시간", color="관측지점") +
                    scaleYContinuous(limits=Pair(4,15) ) +
                    theme(
                        plotTitle= elementText(family="AppleGothic"),
                        axisTextX= elementText(family="AppleGothic", angle=45),
                        axisTitle= elementText(family="AppleGothic"),
                        axisTitleY= elementText(family="AppleGothic"),
                        axisTitleX= elementText(family="AppleGothic" ) ,
                        legendTitle= elementText(family="AppleGothic"),
                        legendText= elementText(family="AppleGothic"),
                        axisTooltip= elementText(family="AppleGothic"),
                        axisTooltipText= elementText(family="AppleGothic"),
                        tooltip= elementText(family="AppleGothic"),
                        tooltipText= elementText(family="AppleGothic"),
                        tooltipTitleText= elementText(family="AppleGothic") ) +
                    ggsize( width = 1200, height = 600)
        }

        LaunchedEffect(key1= seaWaterInfoOneday){
            val data = makeData()
            figureLine = makeLineFigure(data)
        }

        Row(modifier = Modifier.then(modifier).padding(vertical = 8.dp)) {
            PlotPanel(
                modifier = Modifier.size(width = 1200.dp, height = 400.dp),
                figure = figureLine,
                preserveAspectRatio = preserveAspectRatio.value
            ) { computationMessages ->
                computationMessages.forEach { println("[APP MESSAGE] $it") }
            }
        }

    }
}