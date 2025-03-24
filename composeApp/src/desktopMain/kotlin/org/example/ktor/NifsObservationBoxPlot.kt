package org.example.ktor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.letsPlot.asDiscrete
import org.jetbrains.letsPlot.core.spec.plotson.tooltips
import org.jetbrains.letsPlot.geom.geomBoxplot
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.ggsize
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.scale.scaleColorViridis
import org.jetbrains.letsPlot.scale.scaleYContinuous
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import org.jetbrains.letsPlot.themes.elementText
import org.jetbrains.letsPlot.themes.theme
import org.jetbrains.letsPlot.tooltips.layerTooltips

@Composable
fun NifsObservationBoxPlot(modifier: Modifier = Modifier) {

    MaterialTheme {

        val coroutineScope = rememberCoroutineScope()
        val viewModel = remember { NifsBoxPlotViewModel(coroutineScope) }
        val seaWaterInfoOneday = viewModel._seaWaterInfoOneDayStateFlow.collectAsState().value.filter {
            it.gru_nam.equals("동해") and it.obs_lay.equals("1")
        }

        LaunchedEffect(key1 = viewModel){
            viewModel.onEvent(NifsBoxPlotViewModel.Event.ObservationRefresh("oneday"))
        }

        var figure: Plot by remember { mutableStateOf(letsPlot() + geomBoxplot()) }

        val preserveAspectRatio = remember { mutableStateOf(false) }

        fun makeData():Map<String,List<Any>> {

            val sta_nam_kor = mutableListOf<String>()
            val wtr_tmp = mutableListOf<Float>()


            seaWaterInfoOneday.forEach {
                sta_nam_kor.add(it.sta_nam_kor)
                wtr_tmp.add( it.wtr_tmp.trim().toFloat()  )
            }
            return mapOf<String, List<Any>> (
                "ObservatoryName" to sta_nam_kor,
                "Temperature" to wtr_tmp  )
        }

        fun makeFigure(data:Map<String,List<Any>>): Plot {
            val sta_nam_korByMiddle = asDiscrete("ObservatoryName", orderBy = "..middle..", order = 1)

            return letsPlot(data) { y = "Temperature"} +
                    scaleColorViridis(option = "C", end = 0.8) +
                    geomBoxplot{
                        x=sta_nam_korByMiddle
                        color = "..middle.."

                    } +
                  //  scaleYContinuous(limits = Pair(0, 15)) +
                    labs(title="Korea EastSea 수온 일일 통계 정보", y="수온 °C", x="관측지점", color="수온 °C", caption="Nifs") +
                    theme(
                        plotTitle= elementText(family="AppleGothic"),
                        axisText= elementText(family="AppleGothic"),
                        axisTitle= elementText(family="AppleGothic"),
                        axisTitleY= elementText(family="AppleGothic"),
                        axisTitleX= elementText(family="AppleGothic") ,
                        legendTitle= elementText(family="AppleGothic"),
                        legendText= elementText(family="AppleGothic"),
                        axisTooltip= elementText(family="AppleGothic"),
                        axisTooltipText= elementText(family="AppleGothic"),
                        tooltip= elementText(family="AppleGothic"),
                        tooltipText= elementText(family="AppleGothic"),
                        tooltipTitleText= elementText(family="AppleGothic") ) +
                    ggsize(700, 400)

        }

        LaunchedEffect(key1= seaWaterInfoOneday){
            figure = makeFigure(makeData())
        }

        Row(modifier = Modifier.then(modifier).padding(vertical = 8.dp)) {
            PlotPanel(
                modifier = Modifier.size(width = 600.dp, height = 400.dp),
                figure = figure,
                preserveAspectRatio = preserveAspectRatio.value
            ) { computationMessages ->
                computationMessages.forEach { println("[APP MESSAGE] $it") }
            }

        }

    }
}
