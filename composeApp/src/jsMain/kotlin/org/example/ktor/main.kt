package org.example.ktor

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.ktor.data.DATA_DIVISION
import org.example.ktor.data.NifsRepository
import org.example.ktor.model.SeawaterInformationByObservationPoint
import kotlin.js.Json
import kotlin.js.json


import kotlinx.browser.window
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.dom.create

fun List<*>.toGridData():MutableList<Json>{
    val rowData = mutableListOf<Json>()
    this.forEach {
        if (it is SeawaterInformationByObservationPoint ) {
            val item = json(
                "sta_cde" to it.sta_cde,
                "sta_nam_kor" to it.sta_nam_kor,
                "obs_datetime" to it.obs_datetime,
                "obs_lay" to it.obs_lay,
                "wtr_tmp" to it.wtr_tmp,
                "gru_nam" to it.gru_nam,
                "lon" to it.lon,
                "lat" to it.lat
            )
            rowData.add(item)
        }
    }
    return rowData
}


fun main() {



    val repository = getPlatform().nifsRepository

    window.onload = {

        createLayOut{

            setContent(repository, ElementID.ID.LayerBars.division()){
                createContent(ElementID.ID.LayerBars, it)
            }
            setContent(repository, ElementID.ID.BoxPlot.division()){
                createContent(ElementID.ID.BoxPlot, it)
            }

            setContent(repository, ElementID.ID.Line.division()){
                createContent(ElementID.ID.Line, it)
            }

            setContent(repository, ElementID.ID.Ribbon.division()){
                createContent(ElementID.ID.Ribbon, it)
            }
        }


        setGrid(repository) { data ->
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
            val gridDiv = document.getElementById("AgGrid")
            val gridOptions:dynamic = js("({})")
            gridOptions["columnDefs"] = columnDefs
            gridOptions["rowData"] = data.toGridData().toTypedArray()
            gridOptions["pagination"] = true
            js("new agGrid.createGrid(gridDiv, gridOptions)")
        }
    }

}

fun createLayOut( completeHandle:()->Unit) {
    val body = document.body ?: error("No body")
    body.append {

        h1 { +"Nifs Sea Water Temperature Infomation"; style="text-align:center;" }

        h3 {+"Nifs Sea Water Temperature Current Data"}

        div {
            id = "AgGrid";
            style="width: 1340px;height: 300px";
        }

        div { id = "LayerBars"}

        div { id = "BoxPlot"}

        div { id = "Line"}

        div { id = "Ribbon"}

    }
    completeHandle()
}


fun setGrid(
    repository: NifsRepository,
    completeHandle:(result:List<Any>)->Unit
)= CoroutineScope(Dispatchers.Default).launch {
    completeHandle(repository.getSeaWaterInfoValues(DATA_DIVISION.current.name))
}

fun setContent(
    repository: NifsRepository,
    division: DATA_DIVISION,
    completeHandle:(result:List<Any>)->Unit
) = CoroutineScope(Dispatchers.Default).launch {

    val data = when(division){
        DATA_DIVISION.current, DATA_DIVISION.oneday -> {
            repository.getSeaWaterInfoValues(division.name)
        }
        DATA_DIVISION.statistics -> {
            repository.getSeaWaterInfoStatValues()
        }
        else ->{
            emptyList()
        }
    }

    completeHandle(data)
}



