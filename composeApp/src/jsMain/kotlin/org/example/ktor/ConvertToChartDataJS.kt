package org.example.ktor

import org.example.ktor.model.SeawaterInformationByObservationPoint
import kotlin.js.Json
import kotlin.js.json


fun List<*>.toGridData():Array<Json>{

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

    return jsonData.toTypedArray()
}