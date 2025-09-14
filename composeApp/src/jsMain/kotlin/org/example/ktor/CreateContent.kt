package org.example.ktor

import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.ktor.data.DATA_DIVISION
import org.jetbrains.letsPlot.asDiscrete
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
import web.dom.Element as WasmElement

object ContainerDiv {
    enum class ID {
        LayerBars, BoxPlot, Line, Ribbon, AgGridCurrent, ComposeDataGrid, SeaArea, RibbonArea
    }
}

fun ContainerDiv.ID.division(): DATA_DIVISION {
    return when(this){
        ContainerDiv.ID.LayerBars
            -> DATA_DIVISION.current
        ContainerDiv.ID.BoxPlot,
        ContainerDiv.ID.Line,
        ContainerDiv.ID.AgGridCurrent,
        ContainerDiv.ID.SeaArea
            -> DATA_DIVISION.oneday
        ContainerDiv.ID.Ribbon,
        ContainerDiv.ID.RibbonArea
            -> DATA_DIVISION.statistics
        ContainerDiv.ID.ComposeDataGrid
            -> DATA_DIVISION.grid
    }
}

suspend fun loadData( id: DATA_DIVISION) :List<Any> {
    val repository = getPlatform().nifsRepository
    return when(id){
        DATA_DIVISION.current,
        DATA_DIVISION.oneday,
        DATA_DIVISION.grid -> {
            repository.getSeaWaterInfoValues(id.name)
        }
        DATA_DIVISION.statistics -> {
            repository.getSeaWaterInfoStatValues()
        }
    }
}


fun createContent(elementId: ContainerDiv.ID){

    val container: WasmElement =
        document.getElementById(elementId.name) as? WasmElement
            ?: error("Couldn't find ${elementId.name} container!")

    when(elementId) {
        ContainerDiv.ID.LayerBars ->  CoroutineScope(Dispatchers.Default).launch {
            createRoot(container).render(
                SeaWaterInfoChart.create {
                    chartDiv = document.getElementById(elementId.name)
                    dataDivision = elementId.division()
                    loadDataFunction = ::loadData
                    createChartFunction = ::createBarChart
                    chartDataMapper =
                        {  listData, _ -> listData.toLayerBarsData() }
                }
            )

        }
        ContainerDiv.ID.BoxPlot -> CoroutineScope(Dispatchers.Default).launch {
            createRoot(container).render(
                SeaWaterInfoChart.create {
                    chartDiv = document.getElementById(elementId.name)
                    dataDivision = elementId.division()
                    loadDataFunction = ::loadData
                    createChartFunction = ::createBoxPlotChart
                    chartDataMapper =
                        {  listData, _ -> listData.toBoxPlotData() }
                }
            )
        }

        ContainerDiv.ID.AgGridCurrent -> CoroutineScope(Dispatchers.Default).launch {
            createRoot(container).render(
                SeaWaterInfoDataGrid.create {
                    chartDiv = document.getElementById(elementId.name)
                    dataDivision = elementId.division()
                    loadDataFunction = ::loadData
                    createDataGridFunction = ::createGrid
                    gridDataMapper =
                        {  listData -> listData.toGridData() }
                }
            )

        }

        ContainerDiv.ID.SeaArea ->  CoroutineScope(Dispatchers.Default).launch {
            createRoot(container).render(
                SeaAreaLineChart.create {
                    initialSelectedSea =  SEA_AREA.GRU_NAME.entries[1]
                    chartDiv = document.getElementById(ContainerDiv.ID.Line.name)
                    dataDivision = ContainerDiv.ID.Line.division()
                    loadDataFunction = ::loadData
                    createChartFunction = ::createLineChart
                    chartDataMapper =
                        { listData, gruName ->
                            listData.toLineData(gruName ?: SEA_AREA.GRU_NAME.entries[0])
                        }
                }
            )
        }
        ContainerDiv.ID.RibbonArea ->  CoroutineScope(Dispatchers.Default).launch {
            createRoot(container).render(
                SeaAreaRibbonChart.create {
                    initialSelectedSea =  SEA_AREA.GRU_NAME.entries[1]
                    chartDiv = document.getElementById(ContainerDiv.ID.Ribbon.name)
                    dataDivision = ContainerDiv.ID.Ribbon.division()
                    loadDataFunction = ::loadData
                    createChartFunction = ::createRibbonChart
                    chartDataMapper =
                        {  listData, gruName ->
                            listData.toRibbonData(gruName ?: SEA_AREA.GRU_NAME.entries[0])
                        }
                }
            )
        }
        ContainerDiv.ID.Line -> TODO()
        ContainerDiv.ID.Ribbon -> TODO()
        ContainerDiv.ID.ComposeDataGrid -> TODO()
    }

}

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

fun createBarChart(data: Map<String,List<Any>>, entrie:SEA_AREA.GRU_NAME?): Plot {
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

fun createBoxPlotChart(data: Map<String,List<Any>>, entrie:SEA_AREA.GRU_NAME?):Plot{
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


fun createLineChart( data: Map<String,List<Any>>, entrie:SEA_AREA.GRU_NAME?): Plot {
    return letsPlot(data) +
            geomLine { x="CollectingTime"; y="Temperature"; color="ObservatoryName"} +
            labs( title="Korea ${entrie?.name} Sea Water Temperature Line", y="수온 °C", x="관측시간", color="관측지점", caption="Nifs") +
            theme +
            ggsize( width = 1400, height = 400)
}

fun createRibbonChart( data: Map<String,List<Any>>, entrie:SEA_AREA.GRU_NAME?):Plot {
    return letsPlot(data) +
            geomRibbon(alpha = 0.1){
                x="CollectingTime"
                ymin="TemperatureMin"
                ymax="TemperatureMax"
                fill="ObservatoryName"
            } +
            geomLine( showLegend=false ) { x="CollectingTime"; y="TemperatureAvg"; color="ObservatoryName"} +
            labs( title="Korea ${entrie?.name} Sea Water Temperature Ribbon", x="관측시간", y="수온 °C", fill="관측지점", caption="Nifs") +
            theme +
            ggsize(1400, 400)
}