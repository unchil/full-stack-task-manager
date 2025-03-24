package org.example.ktor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

        val coroutineScope = rememberCoroutineScope()

        val viewModel = remember { NifsBarsViewModel(coroutineScope) }

        val seaWaterInfoCurrent = viewModel._seaWaterInfoCurrentStateFlow.collectAsState().value.filter { it.gru_nam.equals("동해") }


        val preserveAspectRatio = remember { mutableStateOf(false) }

        var figure: Plot by remember { mutableStateOf(letsPlot() + geomBar()) }

        fun makeData():Map<String,List<Any>> {

            val sta_nam_kor = mutableListOf<String>()
            val sta_cod = mutableListOf<String>()
            val obs_lay = mutableListOf<String>()
            val wtr_tmp = mutableListOf<Float>()
            val obs_datetime = mutableListOf<String>()

            seaWaterInfoCurrent.forEach {
                sta_cod.add(it.sta_cde)
                obs_datetime.add(it.obs_datetime)
                sta_nam_kor.add(it.sta_nam_kor)
                obs_lay.add(
                    when(it.obs_lay) {
                        "1" -> "표층"
                        "2" -> "중층"
                        "3" -> "저층"
                        else -> {""}
                    }
                )
                wtr_tmp.add( it.wtr_tmp.trim().toFloat()  )
            }
            return mapOf<String, List<Any>> (
                "CollectionTime" to obs_datetime,
                "ObservatoryName" to sta_nam_kor,
                "ObservatoryCode" to sta_cod,
                "ObservatoryDepth" to obs_lay,
                "Temperature" to wtr_tmp  )
        }

        fun makeFigure(data:Map<String,List<Any>>): Plot {
            return letsPlot(data) {
                x = "ObservatoryName"
                weight = "Temperature" } +
                    geomBar(
                        position = positionDodge(),
                        alpha = 0.6,
                        tooltips= layerTooltips()
                            .line("수집시간|@CollectionTime")
                            .line("관측지점|@ObservatoryName/@ObservatoryCode")
                            .line("관측수심|@ObservatoryDepth")
                            .line("온도|^y °C" )
                    ) {
                        fill = "ObservatoryDepth" } +
                    labs( title="Korea EastSea 수온 정보", y="수온 °C", x="관측지점", fill="관측수심", caption="Nifs") +
                    scaleYContinuous(limits = Pair(0, 15)) +
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
                    ggsize(width = 1200, height = 300)
        }


        LaunchedEffect(key1= seaWaterInfoCurrent){
            figure = makeFigure(makeData())
        }



        LaunchedEffect(key1 = viewModel){
            viewModel.onEvent(NifsBarsViewModel.Event.ObservationRefresh("current"))
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

