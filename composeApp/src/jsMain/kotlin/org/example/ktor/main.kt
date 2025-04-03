package org.example.ktor

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.ktor.data.NifsRepository
import org.example.ktor.model.SeawaterInformationByObservationPoint
import org.jetbrains.letsPlot.frontend.JsFrontendUtil
import org.jetbrains.letsPlot.geom.geomBar
import org.jetbrains.letsPlot.ggsize
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.pos.positionDodge
import org.jetbrains.letsPlot.scale.scaleYContinuous
import org.jetbrains.letsPlot.tooltips.layerTooltips


fun main() {

    console.log("[console] Hello, Kotlin/JS!")
    val repository = getPlatform().nifsRepository

    window.onload = {
        getData(repository, "current"){
            createContent(it)
        }
    }
}


fun List<SeawaterInformationByObservationPoint>.toBarChartData():Map<String,List<Any>> {
    console.log("[console] toBarChartData")

    val sta_nam_kor = mutableListOf<String>()
    val sta_cod = mutableListOf<String>()
    val obs_lay = mutableListOf<String>()
    val wtr_tmp = mutableListOf<Float>()
    val obs_datetime = mutableListOf<String>()

    console.log("[console] toBarChartData[${this.count()}]")

    this.forEach {

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


fun getData( repository: NifsRepository, division:String,  completeHandle:(result:List<SeawaterInformationByObservationPoint>)->Unit )
        = CoroutineScope(Dispatchers.Default).launch {
    completeHandle(repository.getSeaWaterInfoValues(division))
}


 fun createContent(seaWaterInfo: List<SeawaterInformationByObservationPoint>) {
    console.log("[console] createContent")
    val contentDiv = document.getElementById("LayerBars")
    val figure = createBarChart(seaWaterInfo.toBarChartData())
    val plotDiv = JsFrontendUtil.createPlotDiv(figure)
    contentDiv?.appendChild(plotDiv)
}


 fun createBarChart(data: Map<String,List<Any>>): Plot {
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
            labs( title="수온 정보", y="수온 °C", x="관측지점", fill="관측수심", caption="Nifs") +
            scaleYContinuous(limits = Pair(0, 15)) +
            ggsize(width = 1200, height = 300)

}

