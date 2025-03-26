package org.example.ktor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.ktor.model.SeaWaterInfoByOneHourStat
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.geom.geomRibbon
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.scale.scaleYContinuous
import org.jetbrains.letsPlot.ggsize
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import org.jetbrains.letsPlot.themes.elementText
import org.jetbrains.letsPlot.themes.theme

@Composable
fun NifsObservationRibbon(modifier: Modifier = Modifier) {
    MaterialTheme {

        val viewModel = remember { NifsRibbonViewModel() }

        val seaWaterInfoStat = viewModel._seaWaterInfoStatStateFlow.collectAsState().value.filter {
            it.gru_nam.equals("동해")
        }

        LaunchedEffect(key1 = viewModel){
            viewModel.onEvent(NifsRibbonViewModel.Event.ObservationStatRefresh)
        }

        var figureLine: Plot by remember { mutableStateOf(letsPlot() + geomRibbon()) }

        val preserveAspectRatio = remember { mutableStateOf(false) }


        fun makeData():Map<String,List<Any>> {

            val gru_nam = mutableListOf<String>()
            val sta_cde = mutableListOf<String>()
            val sta_nam_kor = mutableListOf<String>()
            val obs_datetime = mutableListOf<String>()
            val tmp_min = mutableListOf<Float>()
            val tmp_max = mutableListOf<Float>()
            val tmp_avg = mutableListOf<Float>()

            seaWaterInfoStat.forEach {

                gru_nam.add(it.gru_nam)
                sta_cde.add(it.sta_cde)
                sta_nam_kor.add(it.sta_nam_kor)
                obs_datetime.add(it.obs_datetime)
                tmp_min.add(it.tmp_min.toFloat())
                tmp_max.add(it.tmp_max.toFloat())
                tmp_avg.add(it.tmp_avg.toFloat())

            }
            val data = mapOf<String, List<Any>> (
                "GroupName" to gru_nam,
                "ObservatoryName" to sta_nam_kor,
                "ObservatoryCode" to sta_cde,
                "CollectingTime" to obs_datetime,
                "TemperatureMin" to tmp_min,
                "TemperatureMax" to tmp_max,
                "TemperatureAvg" to tmp_avg,
            )
            return data
        }

        fun makeRibbonFigure(data:Map<String,List<Any>>): Plot {
            return letsPlot(data) +
                    geomRibbon(alpha = 0.1){
                        x="CollectingTime"
                        ymin="TemperatureMin"
                        ymax="TemperatureMax"
                        fill="ObservatoryName"
                    } +
                    geomLine( showLegend=false ) { x="CollectingTime"; y="TemperatureAvg"; color="ObservatoryName"} +
                 //   scaleYContinuous(limits=Pair(10,12.5) ) +
                    labs(title="Korea EastSea Water Quality Ribbon", x="관측시간", y="수온 °C", fill="관측지점", caption="Nifs") +
                    theme(
                        plotTitle= elementText(),
                        axisTextX= elementText( angle=45),
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
                    ggsize(1200, 800)
        }

        LaunchedEffect(key1= seaWaterInfoStat){
            val data = makeData()
            figureLine = makeRibbonFigure(data)
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