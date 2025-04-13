package org.example.ktor

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.dom.append
import org.example.ktor.data.DATA_DIVISION
import org.example.ktor.data.NifsRepository
import org.example.ktor.model.SeawaterInformationByObservationPoint
import kotlin.js.Json
import kotlin.js.json

fun main() {

    val repository = getPlatform().nifsRepository

    window.onload = {
        createLayOut{
            setContent(repository, ElementID.ID.LayerBars)
            setContent(repository, ElementID.ID.BoxPlot)
            setContent(repository, ElementID.ID.Line)
            setContent(repository, ElementID.ID.Ribbon)
            setContent(repository, ElementID.ID.AgGridCurrent)
        }
    }

}

fun createLayOut( completeHandle:()->Unit) {
    val body = document.body ?: error("No body")
    body.append {

        h1 { +"Nifs Sea Water Temperature Infomation"; style="text-align:center;" }

        h3 {+"Nifs Sea Water Temperature OneDay Data"}

        div {
            id = ElementID.ID.AgGridCurrent.name
            style="width: 1360px;height: 300px"
        }

        div { id = ElementID.ID.LayerBars.name}

        div { id = ElementID.ID.BoxPlot.name}

        div { id = ElementID.ID.Line.name}

        div { id = ElementID.ID.Ribbon.name}

    }
    completeHandle()
}


fun setContent(
    repository: NifsRepository,
    id: ElementID.ID
) = CoroutineScope(Dispatchers.Default).launch {

    val data = when(id.division()){
        DATA_DIVISION.current, DATA_DIVISION.oneday -> {
            repository.getSeaWaterInfoValues(id.division().name)
        }
        DATA_DIVISION.statistics -> {
            repository.getSeaWaterInfoStatValues()
        }
        else ->{
            emptyList()
        }
    }

    createContent(id, data)

}

fun List<*>.toGridData():MutableList<Json>{

    val rawData = mutableListOf<SeawaterInformationByObservationPoint>()

    this.forEach {
        if(it is SeawaterInformationByObservationPoint){
            rawData.add(it)
        }
    }

    val jsonData = mutableListOf<Json>()

    rawData.sortedByDescending { it.obs_datetime }
        .forEach {
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
            jsonData.add(item)

        }

    return jsonData
}