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

    window.onload = {
        getData(getPlatform().nifsRepository){
            createContent(it)
        }
    }
}


fun getData(repogitory: NifsRepository, completeHandle:(result:List<SeawaterInformationByObservationPoint>)->Unit )
        = CoroutineScope(Dispatchers.Default).launch {
    completeHandle(repogitory.getSeaWaterInfoValues("current"))
}


 fun createContent(seaWaterInfo: List<SeawaterInformationByObservationPoint>) {
    console.log("[console] createContent")
    val contentDiv = document.getElementById("content")
    val figure = makeFigure(seaWaterInfo)
    val plotDiv = JsFrontendUtil.createPlotDiv(figure)
    contentDiv?.appendChild(plotDiv)
}


 fun toMap(seaWaterInfo: List<SeawaterInformationByObservationPoint>):Map<String,List<Any>> {
    console.log("[console] makeData")

    val sta_nam_kor = mutableListOf<String>()
    val sta_cod = mutableListOf<String>()
    val obs_lay = mutableListOf<String>()
    val wtr_tmp = mutableListOf<Float>()
    val obs_datetime = mutableListOf<String>()

    console.log("[console] seaWaterInfo[${seaWaterInfo.count()}]")

     seaWaterInfo.forEach {

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


 fun makeFigure(seaWaterInfo: List<SeawaterInformationByObservationPoint>): Plot {
    return letsPlot(toMap(seaWaterInfo)) {
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
            ggsize(width = 1200, height = 300)

}

