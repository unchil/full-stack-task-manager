package org.example.ktor

import kotlinx.browser.document
import org.example.ktor.data.DATA_DIVISION
import org.example.ktor.model.SeawaterInformationByObservationPoint
import org.jetbrains.letsPlot.asDiscrete
import org.jetbrains.letsPlot.frontend.JsFrontendUtil
import org.jetbrains.letsPlot.geom.geomBar
import org.jetbrains.letsPlot.geom.geomBoxplot
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.geom.geomRibbon
import org.jetbrains.letsPlot.ggsize
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.pos.positionDodge
import org.jetbrains.letsPlot.scale.scaleColorViridis
import org.jetbrains.letsPlot.scale.scaleYContinuous
import org.jetbrains.letsPlot.themes.elementText
import org.jetbrains.letsPlot.themes.theme
import org.jetbrains.letsPlot.tooltips.layerTooltips
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import kotlin.js.Json
import kotlin.js.json


object ElementID {
    enum class ID {
        LayerBars, BoxPlot, Line, Ribbon, AgGridCurrent
    }
    val IDs = listOf(
        ID.LayerBars, ID.BoxPlot, ID.Line, ID.Ribbon, ID.AgGridCurrent
    )
}

fun ElementID.ID.division(): DATA_DIVISION {
    return when(this){
        ElementID.ID.LayerBars -> DATA_DIVISION.current
        ElementID.ID.BoxPlot -> DATA_DIVISION.oneday
        ElementID.ID.Line -> DATA_DIVISION.oneday
        ElementID.ID.Ribbon -> DATA_DIVISION.statistics
        ElementID.ID.AgGridCurrent -> DATA_DIVISION.current
    }
}


fun createContent(elementId: ElementID.ID, data:List<Any>)   {

    when(elementId) {

            ElementID.ID.LayerBars -> {
                document.getElementById(ElementID.ID.LayerBars.name)?.appendChild(
                    JsFrontendUtil.createPlotDiv(
                        createBarChart(data.toLayerBarsData())
                    )
                )
            }

            ElementID.ID.BoxPlot -> {
                document.getElementById(ElementID.ID.BoxPlot.name)?.appendChild(
                    JsFrontendUtil.createPlotDiv(
                        createBoxPlotChart(data.toBoxPlotData())
                    )
                )
            }

            ElementID.ID.Line -> {
                document.getElementById(ElementID.ID.Line.name)?.appendChild(
                    JsFrontendUtil.createPlotDiv(
                        createLineChart(data.toLineData())
                    )
                )
            }

            ElementID.ID.Ribbon -> {
                document.getElementById(ElementID.ID.Ribbon.name)?.appendChild(
                    JsFrontendUtil.createPlotDiv(
                        createRibbonChart(data.toRibbonData())
                    )
                )
            }

            ElementID.ID.AgGridCurrent -> {
                document.getElementById(ElementID.ID.AgGridCurrent.name)?.let {
                    createGrid(it, data.toGridData().toTypedArray())
                }
            }
        }

}


fun createGrid(gridDiv:Element, data:Array<Json>)  {

    val columnDefs = arrayOf(
        json( "field" to "sta_cde", "width" to 150),
        json( "field" to "sta_nam_kor", "width" to 150),
        json( "field" to "obs_datetime", "width" to 200),
        json( "field" to "obs_lay", "width" to 150),
        json( "field" to "wtr_tmp", "width" to 150),
        json( "field" to "obs_lay", "width" to 150),
        json( "field" to "gru_nam", "width" to 150),
        json( "field" to "lon", "width" to 150),
        json( "field" to "lat", "width" to 150)
    )

    val gridOptions:dynamic = js("({})")
    gridOptions["columnDefs"] = columnDefs
    gridOptions["rowData"] = data
    gridOptions["pagination"] = true
    gridOptions["paginationPageSize"] = 20
    gridOptions["paginationPageSizeSelector"] = arrayOf(20, 100, 1000)

    js("new agGrid.createGrid(gridDiv, gridOptions)")
}



val theme = theme( axisTextX= elementText( angle=45))

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
            labs( title="실시간 수온 정보", y="수온 °C", x="관측지점", fill="관측수심", caption="Nifs") +
            scaleYContinuous(limits = Pair(0, 15)) +
            theme +
            ggsize(width = 1400, height = 400)

}

fun createBoxPlotChart(data: Map<String,List<Any>>):Plot{
    val sta_nam_korByMiddle = asDiscrete("ObservatoryName", orderBy = "..middle..", order = 1)
    return letsPlot(data)  +
            scaleColorViridis(option = "C", end = 0.8) +
            geomBoxplot{
                x= sta_nam_korByMiddle
                y="Temperature"
                color = "..middle.."
            } +
            scaleYContinuous(limits = Pair(0, 15)) +
            labs(title="수온 일일 통계 정보", y="수온 °C", x="관측지점", color="수온 °C", caption="Nifs") +
            theme +
            ggsize(1400, 400)
}

fun createLineChart(data: Map<String,List<Any>>):Plot {
    return letsPlot(data) +
            geomLine { x="CollectingTime"; y="Temperature"; color="ObservatoryName"} +
            labs( title="Korea EastSea Water Temperature Line", y="수온 °C", x="관측시간", color="관측지점", caption="Nifs") +
            scaleYContinuous(limits=Pair(4,15) ) +
            theme +
            ggsize( width = 1400, height = 400)
}

fun createRibbonChart(data: Map<String,List<Any>>):Plot {
    return letsPlot(data) +
            geomRibbon(alpha = 0.1){
                x="CollectingTime"
                ymin="TemperatureMin"
                ymax="TemperatureMax"
                fill="ObservatoryName"
            } +
            geomLine( showLegend=false ) { x="CollectingTime"; y="TemperatureAvg"; color="ObservatoryName"} +
            scaleYContinuous(limits=Pair(4,15) ) +
            labs(title="Korea EastSea Water Temperature Ribbon", x="관측시간", y="수온 °C", fill="관측지점", caption="Nifs") +
            theme +
            ggsize(1400, 400)
}