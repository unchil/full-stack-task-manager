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
import androidx.compose.ui.window.application
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import kotlinx.io.bytestring.encodeToByteString
import org.jetbrains.compose.resources.getFontResourceBytes
import org.jetbrains.letsPlot.*
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults
import org.jetbrains.letsPlot.core.spec.plotson.theme
import org.jetbrains.letsPlot.font.fontFamilyInfo
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
import org.jetbrains.letsPlot.themes.theme
import org.jetbrains.letsPlot.tooltips.TooltipOptions
import org.jetbrains.letsPlot.tooltips.layerTooltips
import org.jetbrains.skia.Font
import java.awt.GraphicsEnvironment

val state = WindowState(
    size = DpSize(1200.dp, 620.dp),
    position = WindowPosition(Alignment.Center)
)

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "동해안 바닷물 정보",
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
            var plot = letsPlot() + geomPoint()
            plot += xlim(listOf(-3.0, 3.0)) + ylim(listOf(0.0, 0.5))
            return plot
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
                sta_nam_kor.add(
                    when(it.sta_nam_kor){
                        "기장" -> "Gijang"
                        "강릉" -> "Gangneung"
                        "고성 가진" -> "Goseong Gajin"
                        "구룡포 하정" -> "Guryongpo Hajeong"
                        "삼척" -> "Samcheok"
                        "영덕" -> "Yeongdeok"
                        "양양" -> "Yangyang"
                        else -> ""
                    }
                )
                obs_lay.add(
                    when(it.obs_lay) {
                        "1" -> "Surface"
                        "2" -> "Middle"
                        "3" -> "Low"
                        else -> {""}
                    }
                )
                wtr_tmp.add( it.wtr_tmp.trim().toFloat()  )
            }

            val data = mapOf<String, List<Any>>("CollectionTime" to obs_datetime, "ObservationName" to sta_nam_kor, "ObservationCode" to sta_cod,"ObservationDepth" to obs_lay, "Temperature" to wtr_tmp  )

            figure = letsPlot(data,) { x = "ObservationName" } +
                    geomBar(
                        position = positionDodge(),
                        alpha = 0.5,
                        tooltips= layerTooltips()
                         .line("@|@CollectionTime")
                         .line("ObservationPoint|@ObservationName/@ObservationCode")
                         .line("@|@ObservationDepth")
                         .line("Temperature|^y °C" )

                    ) {
                        fill = "ObservationDepth"
                        weight = "Temperature"
                    } +
                    fontFamilyInfo("AppleGothic") +
                    labs(title = "Korea EastSea Water 정보", y = "Temperature °C") +
                    scaleYContinuous(limits = Pair(0, 15)) +
                    ggsize(width = 1200, height = 300)

        }


        Row(modifier = Modifier.fillMaxSize().padding(8.dp)) {

            LazyColumn(modifier = Modifier.size(width = 360.dp, height = 600.dp)) {
                items(seaWaterInfoCurrent.count()
                ) { index ->
                    Text("${seaWaterInfoCurrent[index].sta_nam_kor}:${seaWaterInfoCurrent[index].sta_cde}:${seaWaterInfoCurrent[index].obs_lay}:${seaWaterInfoCurrent[index].wtr_tmp}:${seaWaterInfoCurrent[index].lon} :${seaWaterInfoCurrent[index].lat}"  )
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