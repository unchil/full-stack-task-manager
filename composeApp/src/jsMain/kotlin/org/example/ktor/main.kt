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

        val rowData = mutableListOf<Any>()

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
          //  gridOptions["theme"] = "themeAlpine"
            js("new agGrid.createGrid(gridDiv, gridOptions)")

        }

    }

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



