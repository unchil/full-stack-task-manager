package org.example.ktor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.jetbrains.letsPlot.geom.geomBar
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.geom.geomText
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

val state = WindowState(
    size = DpSize(1200.dp, 620.dp),
    position = WindowPosition(Alignment.Center)
)

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "NIFS SeaWater Infomation",
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

        val seaWaterInfoCurrent = viewModel._seaWaterInfoCurrentStateFlow.collectAsState().value.filter {
            it.gru_nam.equals("동해")
        }

        val preserveAspectRatio = remember { mutableStateOf(false) }

        fun blankPlot(): Plot {
            return letsPlot() + geomPoint()
        }

        var figure: Plot by remember { mutableStateOf(blankPlot()) }

        LaunchedEffect(key1= viewModel._seaWaterInfoCurrentStateFlow.collectAsState().value){

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

            val data = mapOf<String, List<Any>> (
                "CollectionTime" to obs_datetime,
                "ObservatoryName" to sta_nam_kor,
                "ObservatoryCode" to sta_cod,
                "ObservatoryDepth" to obs_lay,
                "Temperature" to wtr_tmp  )

            figure = letsPlot(data) {
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
                labs( title="Korea EastSea 수온 정보", y="온도 °C", x="관측지점", fill="관측수심", caption="Nifs") +
                scaleYContinuous(limits = Pair(0, 15)) +
                theme(
                    plotTitle=elementText(family="AppleGothic"),
                    axisText=elementText(family="AppleGothic"),
                    axisTitle=elementText(family="AppleGothic"),
                    axisTitleY=elementText(family="AppleGothic"),
                    axisTitleX=elementText(family="AppleGothic") ,
                    legendTitle=elementText(family="AppleGothic"),
                    legendText=elementText(family="AppleGothic"),
                    axisTooltip=elementText(family="AppleGothic"),
                    axisTooltipText=elementText(family="AppleGothic"),
                    tooltip=elementText(family="AppleGothic"),
                    tooltipText=elementText(family="AppleGothic"),
                    tooltipTitleText=elementText(family="AppleGothic"),

                ) +
                ggsize(width = 1200, height = 300)

               // fontFamilyInfo("AppleGothic")

        }

        Row(modifier = Modifier.fillMaxSize().padding(8.dp)) {

            Column(modifier = Modifier.size(width = 360.dp, height = 600.dp)){



                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(seaWaterInfoCurrent.count()
                    ) { index ->
                        Text("${seaWaterInfoCurrent[index].sta_nam_kor}:${seaWaterInfoCurrent[index].sta_cde}:${seaWaterInfoCurrent[index].obs_lay}:${seaWaterInfoCurrent[index].wtr_tmp}:${seaWaterInfoCurrent[index].lon} :${seaWaterInfoCurrent[index].lat}"  )
                    }
                }

            }


            PlotPanel(
                figure = figure,
                preserveAspectRatio = preserveAspectRatio.value,
                modifier = Modifier.size(width = 840.dp, height = 600.dp)
            ) { computationMessages ->
                computationMessages.forEach { println("[APP MESSAGE] $it") }
            }


        }

    }

}