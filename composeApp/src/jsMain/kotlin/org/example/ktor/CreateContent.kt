package org.example.ktor



import kotlinx.browser.document
import org.example.ktor.data.DATA_DIVISION
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
import org.jetbrains.letsPlot.themes.elementText
import org.jetbrains.letsPlot.themes.theme
import org.jetbrains.letsPlot.tooltips.layerTooltips
import org.w3c.dom.Element
import react.create
import react.dom.client.createRoot
import kotlin.js.Json
import kotlin.js.json

import web.dom.Element as WasmElement // web.dom.Element의 별칭 사용

object ContainerDiv {
    enum class ID {
        LayerBars, BoxPlot, Line, Ribbon, AgGridCurrent, ComposeDataGrid, SeaArea, RibbonArea
    }
    val IDs = listOf(
        ID.LayerBars, ID.BoxPlot, ID.Line, ID.Ribbon, ID.AgGridCurrent, ID.ComposeDataGrid, ID.SeaArea, ID.RibbonArea
    )
}

fun ContainerDiv.ID.division(): DATA_DIVISION {
    return when(this){
        ContainerDiv.ID.LayerBars -> DATA_DIVISION.current
        ContainerDiv.ID.BoxPlot -> DATA_DIVISION.oneday
        ContainerDiv.ID.Line -> DATA_DIVISION.oneday
        ContainerDiv.ID.Ribbon -> DATA_DIVISION.statistics
        ContainerDiv.ID.AgGridCurrent -> DATA_DIVISION.oneday
        ContainerDiv.ID.ComposeDataGrid -> DATA_DIVISION.grid
        ContainerDiv.ID.SeaArea -> DATA_DIVISION.oneday
        ContainerDiv.ID.RibbonArea -> DATA_DIVISION.statistics
    }
}

fun createContent(elementId: ContainerDiv.ID, data:List<Any>)   {

    val selectedOptionLine:SEA_AREA.GRU_NAME = SEA_AREA.GRU_NAME.entries[1]

    when(elementId) {

        ContainerDiv.ID.LayerBars -> {
            document.getElementById(ContainerDiv.ID.LayerBars.name)?.appendChild(
                JsFrontendUtil.createPlotDiv(
                    createBarChart(data.toLayerBarsData())
                )
            )
        }

        ContainerDiv.ID.BoxPlot -> {
            document.getElementById(ContainerDiv.ID.BoxPlot.name)?.appendChild(
                JsFrontendUtil.createPlotDiv(
                    createBoxPlotChart(data.toBoxPlotData())
                )
            )
        }


        ContainerDiv.ID.Line -> {
            document.getElementById(ContainerDiv.ID.Line.name)?.appendChild(
                JsFrontendUtil.createPlotDiv(
                    createLineChart(selectedOptionLine, data.toLineData(selectedOptionLine))
                )
            )
        }

        ContainerDiv.ID.Ribbon -> {
            document.getElementById(ContainerDiv.ID.Ribbon.name)?.appendChild(
                JsFrontendUtil.createPlotDiv(
                    createRibbonChart(selectedOptionLine, data.toRibbonData(selectedOptionLine))
                )
            )
        }



        ContainerDiv.ID.AgGridCurrent -> {
            document.getElementById(ContainerDiv.ID.AgGridCurrent.name)?.let {

                createGrid(it, data.toGridData().toTypedArray())
            }
        }

        ContainerDiv.ID.ComposeDataGrid -> {

        }


        ContainerDiv.ID.SeaArea -> {
            val container: WasmElement =
                document.getElementById(ContainerDiv.ID.SeaArea.name) as? WasmElement
                    ?: error("Couldn't find ${ContainerDiv.ID.SeaArea.name} container!")

            // SeaAreaRadioBtn 컴포넌트 렌더링
            // selectedOptionLine은 이제 컴포넌트 내부 상태로 관리되므로, 초기값만 전달

            createRoot(container).render(
                SeaAreaRadioBtn.create {
                    initialSelectedSea = selectedOptionLine // 기존 selectedOptionLine을 초기값으로 전달
                    chartDiv = document.getElementById(ContainerDiv.ID.Line.name)
                    chartData = data // 전체 원본 데이터 전달
                    createLineChartFunction = ::createLineChart // createLineChart 함수 자체를 전달
                    lineChartDataMapper =
                        { sea, listData -> listData.toLineData(sea) } // 데이터 변환 로직 전달
                }
            )
        }

        ContainerDiv.ID.RibbonArea -> {
            val container: WasmElement =
                document.getElementById(ContainerDiv.ID.RibbonArea.name) as? WasmElement
                    ?: error("Couldn't find ${ContainerDiv.ID.RibbonArea.name} container!")

                createRoot(container).render(
                RibbonAreaRadioBtn.create {
                    initialSelectedSea = selectedOptionLine
                    chartDiv = document.getElementById(ContainerDiv.ID.Ribbon.name)
                    chartData = data
                    createLineChartFunction = ::createRibbonChart
                    lineChartDataMapper =
                        { sea, listData -> listData.toRibbonData(sea) }
                }
            )
        }

    }

}

// import org.w3c.dom.Element
fun createGrid(gridDiv: Element, data:Array<Json>)  {
    val columnDefs = arrayOf(
        json( "field" to "sta_cde", "width" to 150),
        json( "field" to "sta_nam_kor", "width" to 150),
        json( "field" to "obs_datetime", "width" to 200),
        json( "field" to "obs_lay",  "width" to 150),
        json( "field" to "wtr_tmp","width" to 150),
        json( "field" to "gru_nam",  "width" to 150),
        json( "field" to "lon", "width" to 150),
        json( "field" to "lat", "width" to 150)
    )
    val gridOptions:dynamic = js("({})")
    gridOptions["columnDefs"] = columnDefs
    gridOptions["defaultColDef"] = json("filter" to true)
    gridOptions["rowData"] = data
    gridOptions["pagination"] = true
    gridOptions["paginationPageSize"] = 20
    gridOptions["paginationPageSizeSelector"] = arrayOf(20, 100, 1000)

    js( " gridApi = agGrid.createGrid(gridDiv, gridOptions)")

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
            labs(title="수온 일일 통계 정보", y="수온 °C", x="관측지점", color="수온 °C", caption="Nifs") +
            theme +
            ggsize(1400, 400)
}


fun createLineChart(entrie:SEA_AREA.GRU_NAME, data: Map<String,List<Any>>): Plot {
    return letsPlot(data) +
            geomLine { x="CollectingTime"; y="Temperature"; color="ObservatoryName"} +
            labs( title="Korea "+ entrie.name +" Sea Water Temperature Line", y="수온 °C", x="관측시간", color="관측지점", caption="Nifs") +
            theme +
            ggsize( width = 1400, height = 400)
}

fun createRibbonChart(entrie:SEA_AREA.GRU_NAME, data: Map<String,List<Any>>):Plot {
    return letsPlot(data) +
            geomRibbon(alpha = 0.1){
                x="CollectingTime"
                ymin="TemperatureMin"
                ymax="TemperatureMax"
                fill="ObservatoryName"
            } +
            geomLine( showLegend=false ) { x="CollectingTime"; y="TemperatureAvg"; color="ObservatoryName"} +
            labs( title="Korea "+ entrie.name +" Sea Water Temperature Ribbon", x="관측시간", y="수온 °C", fill="관측지점", caption="Nifs") +
            theme +
            ggsize(1400, 400)
}