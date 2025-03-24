package org.example.ktor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NifsAndroid()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    NifsAndroid()
}


@Composable
fun NifsAndroid(){



    MaterialTheme {


        val scope = rememberCoroutineScope()


        val viewModel = remember {
            NifsBarsViewModel(scope)
        }

        val seaWaterInfoCurrent = viewModel._seaWaterInfoCurrentStateFlow.collectAsState().value

        val preserveAspectRatio = remember { mutableStateOf(false) }
/*
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
                        "1" -> "Surface"
                        "2" -> "Middle"
                        "3" -> "Low"
                        else -> {""}
                    }
                )
                wtr_tmp.add(it.wtr_tmp.toFloat())
            }

            val data = mapOf<String, List<Any>>( "ObservationPoint" to sta_nam_kor, "ObservationDepth" to obs_lay, "Temperature" to wtr_tmp  )

            figure = letsPlot(data,) { x = "ObservationPoint" } +
                    geomBar(position = positionDodge(), alpha = 0.5) {
                        fill = "ObservationDepth"
                        weight = "Temperature"
                    } +
                    fontFamilyInfo("AppleGothic") +
                    labs(title = "Korea Sea Water Quality", y = "Temperature °C") +
                    scaleYContinuous(limits = Pair(0, 15)) +
                    ggsize(width = 1200, height = 300)

        }
*/

        Column (modifier = Modifier.fillMaxSize().padding(8.dp)) {


            LazyColumn (modifier = Modifier.fillMaxSize()) {
                items(seaWaterInfoCurrent.count()) { index ->
                    Text("${seaWaterInfoCurrent[index].sta_nam_kor}:${seaWaterInfoCurrent[index].obs_lay}:${seaWaterInfoCurrent[index].wtr_tmp}:${seaWaterInfoCurrent[index].lon} :${seaWaterInfoCurrent[index].lat}"  )
                }
            }


            /*

            PlotPanel(
                figure = figure,
                preserveAspectRatio = preserveAspectRatio.value,
                modifier = Modifier.fillMaxSize()
            ) { computationMessages ->
                computationMessages.forEach { println("[APP MESSAGE] $it") }
            }

             */


        }

    }

}