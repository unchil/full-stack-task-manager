package org.example.ktor

import kotlinx.browser.document
import org.example.ktor.SEA_AREA.gru_nam
import org.example.ktor.WATER_QUALITY.desc
import org.example.ktor.WATER_QUALITY.name
import org.example.ktor.WATER_QUALITY.unit
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
import org.jetbrains.letsPlot.scale.scaleXDateTime
import org.jetbrains.letsPlot.scale.scaleYContinuous
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
        LayerBars, BoxPlot, Line, MofSeaQuality, Ribbon, AgGridCurrent, ComposeDataGrid, SeaArea, RibbonArea, MofSeaQualityArea
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
        ContainerDiv.ID.MofSeaQuality,
        ContainerDiv.ID.MofSeaQualityArea
            -> DATA_DIVISION.mof_oneday
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
        DATA_DIVISION.mof_oneday -> {
            repository.getSeaWaterInfoMof(id.name)
        }
    }
}


fun createContent(elementId: ContainerDiv.ID){

    val container: WasmElement =
        document.getElementById(elementId.name) as? WasmElement
            ?: error("Couldn't find ${elementId.name} container!")

    when(elementId) {
        ContainerDiv.ID.LayerBars ->  {
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
        ContainerDiv.ID.BoxPlot ->  {
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

        ContainerDiv.ID.AgGridCurrent -> {
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

        ContainerDiv.ID.SeaArea ->  {
            createRoot(container).render(
                SeaAreaLineChart.create {
                    initialSelectedSea =  SEA_AREA.GRU_NAME.entries[1].name
                    chartDiv = document.getElementById(ContainerDiv.ID.Line.name)
                    dataDivision = ContainerDiv.ID.Line.division()
                    loadDataFunction = ::loadData
                    createChartFunction = ::createLineChart
                    chartDataMapper =
                        { listData, gruName ->
                            listData.toLineData(gruName ?: SEA_AREA.GRU_NAME.entries[0].gru_nam() )
                        }
                }
            )
        }
        ContainerDiv.ID.RibbonArea -> {
            createRoot(container).render(
                SeaAreaLineChart.create {
                    initialSelectedSea =  SEA_AREA.GRU_NAME.entries[1].name
                    chartDiv = document.getElementById(ContainerDiv.ID.Ribbon.name)
                    dataDivision = ContainerDiv.ID.Ribbon.division()
                    loadDataFunction = ::loadData
                    createChartFunction = ::createRibbonChart
                    chartDataMapper =
                        {  listData, gruName ->
                            listData.toRibbonData(gruName ?: SEA_AREA.GRU_NAME.entries[0].gru_nam())
                        }
                }
            )
        }
        ContainerDiv.ID.Line -> TODO()
        ContainerDiv.ID.Ribbon -> TODO()
        ContainerDiv.ID.ComposeDataGrid -> TODO()
        ContainerDiv.ID.MofSeaQuality -> TODO()
        ContainerDiv.ID.MofSeaQualityArea -> {
            createRoot(container).render(
                MofSeaQualityAreaLineChart.create {
                    initialSelectedType =  WATER_QUALITY.QualityType.entries[0].name()
                    chartDiv = document.getElementById(ContainerDiv.ID.MofSeaQuality.name)
                    dataDivision = ContainerDiv.ID.MofSeaQuality.division()
                    loadDataFunction = ::loadData
                    createChartFunction = ::createMofSeaQualityChart
                    chartDataMapper =
                        { listData, qualityType ->
                            listData.toMofLineData(qualityType)
                        }
                }
            )
        }
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

    js( "agGrid.createGrid(gridDiv, gridOptions)")

}

val theme = theme( axisTextX= elementText( angle=45))

fun createBarChart(data: Map<String,List<Any>>, entrie:String?): Plot {

    val yMin: Float? = data.getValue("Temperature").minOfOrNull {
        it as Float
    }
    val yMax: Float? = data.getValue("Temperature").maxOfOrNull {
        it as Float
    }

    return letsPlot(data) {
        x = "ObservatoryName"
        weight = "Temperature"
    } + geomBar(
            position = positionDodge(),
            alpha = 0.6,
            tooltips= layerTooltips()
                .line("수집시간|@CollectionTime")
                .line("관측지점|@ObservatoryName/@ObservatoryCode")
                .line("관측수심|@ObservatoryDepth")
                .line("온도|^y °C" )
        ) {
            fill = "ObservatoryDepth"
    } + labs( title="실시간 수온 정보", y="수온 °C", x="관측지점", fill="관측수심", caption="Nifs") +
    theme +
    scaleYContinuous(
        limits =  (yMin?.minus(0.5) ?: 0.0) to (yMax?.plus(0.5) ?: 0.0),
        breaks = ( (yMin?.toInt()?:0).. (yMax?.toInt()?:1) ).toList(),
        format = ".1f"
    ) +
    ggsize(width = 1400, height = 400)
}

fun createBoxPlotChart(data: Map<String,List<Any>>, entrie:String?):Plot{

    return letsPlot(data)  +
            scaleColorViridis(option = "C", end = 0.8) +
            geomBoxplot{
                x= asDiscrete("ObservatoryName", order = 1)
                y= "Temperature"
                color = "..middle.."
            } +
            labs(title="수온 일일 통계 정보", y="수온 °C", x="관측지점", color="수온 °C", caption="Nifs") +
            theme +
            ggsize(1400, 400)
}


fun createLineChart( data: Map<String,List<Any>>, entrie:String?): Plot {
    return letsPlot(data) +
            geomLine { x="CollectingTime"; y="Temperature"; color="ObservatoryName"} +
            labs( title="Korea ${entrie?: ""} Sea Water Temperature Line", y="수온 °C", x="관측시간", color="관측지점", caption="Nifs") +
            theme +
            ggsize( width = 1400, height = 400)
}

fun createMofSeaQualityChart(data: Map<String,List<Any>>, qualityType: WATER_QUALITY.QualityType): Plot {
    return letsPlot(data) +
            geomLine { x="CollectingTime"; y="Value"; color="ObservatoryName"} +
            scaleXDateTime(
                format = "%d %H" // 원하는 "dd HH" 형식 지정
            ) +
            labs( title = qualityType.name(), subtitle = qualityType.desc(), y= qualityType.unit(), x="관측일시", color="관측지점", caption=WATER_QUALITY.caption) +
            theme +
            ggsize( width = 1400, height = 680)
}


fun createRibbonChart( data: Map<String,List<Any>>, entrie:String?):Plot {
    val yMin: Float? = data.getValue("TemperatureMin").minOfOrNull {
        it as Float
    }
    val yMax: Float? = data.getValue("TemperatureMax").maxOfOrNull {
        it as Float
    }

    return letsPlot(data) +
            geomRibbon(alpha = 0.1){
                x="CollectingTime"
                ymin="TemperatureMin"
                ymax="TemperatureMax"
                fill="ObservatoryName"
            } +
            geomLine( showLegend=false ) { x="CollectingTime"; y="TemperatureAvg"; color="ObservatoryName"} +
            scaleYContinuous(
                limits =  (yMin?.minus(0.5) ?: 0.0) to (yMax?.plus(0.5) ?: 0.0),
                breaks = ( (yMin?.toInt()?:0).. (yMax?.toInt()?:1) ).toList(),
                format = ".1f"
            ) +
            labs( title="Korea ${entrie?:""} Sea Water Temperature Ribbon", x="관측시간", y="수온 °C", fill="관측지점", caption="Nifs") +
            theme +
            ggsize(1400, 400)
}

