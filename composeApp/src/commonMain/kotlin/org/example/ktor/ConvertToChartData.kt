package org.example.ktor

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import org.example.ktor.SEA_AREA.gru_nam
import org.example.ktor.model.SeaWaterInfoByOneHourStat
import org.example.ktor.model.SeawaterInformationByObservationPoint


fun List<*>.toLayerBarsData():Map<String,List<Any>> {
    val sta_nam_kor = mutableListOf<String>()
    val sta_cod = mutableListOf<String>()
    val obs_lay = mutableListOf<String>()
    val wtr_tmp = mutableListOf<Float>()
    val obs_datetime = mutableListOf<String>()


    this.forEach {
        if( it is SeawaterInformationByObservationPoint ) {
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
    }

    return mapOf<String, List<Any>> (
        "CollectionTime" to obs_datetime,
        "ObservatoryName" to sta_nam_kor,
        "ObservatoryCode" to sta_cod,
        "ObservatoryDepth" to obs_lay,
        "Temperature" to wtr_tmp  )

}


fun List<*>.toBoxPlotData():Map<String,List<Any>> {
    val sta_nam_kor = mutableListOf<String>()
    val wtr_tmp = mutableListOf<Float>()
    val obs_datetime = mutableListOf<String>()

    this.forEach {
        if( it is SeawaterInformationByObservationPoint && it.obs_lay.equals("1")) {
            sta_nam_kor.add(it.sta_nam_kor)
            obs_datetime.add(it.obs_datetime)
            wtr_tmp.add(it.wtr_tmp.trim().toFloat())
        }
    }

    return mapOf<String, List<Any>> (
        "CollectingTime" to obs_datetime,
        "ObservatoryName" to sta_nam_kor,
        "Temperature" to wtr_tmp  )
}

@OptIn(FormatStringsInDatetimeFormats::class)
fun List<*>.toLineData(gruName: String):Map<String,List<Any>> {
    val dateTimeFormatInput = LocalDateTime.Format { byUnicodePattern("yyyy-MM-dd HH:mm:ss") }
    val dateTimeFormatOuput = LocalDateTime.Format { byUnicodePattern("yy/MM/dd HH:mm") }

    val sta_nam_kor = mutableListOf<String>()
    val wtr_tmp = mutableListOf<Float>()
    val obs_datetime = mutableListOf<String>()

    this.forEach {
        if (it is SeawaterInformationByObservationPoint && it.gru_nam.equals(gruName) && it.obs_lay.equals("1")) {
            sta_nam_kor.add(it.sta_nam_kor)
            obs_datetime.add(dateTimeFormatInput.parse(it.obs_datetime).format(dateTimeFormatOuput))
            wtr_tmp.add(it.wtr_tmp.trim().toFloat())
        }
    }

    return mapOf<String, List<Any>>(
        "CollectingTime" to obs_datetime,
        "ObservatoryName" to sta_nam_kor,
        "Temperature" to wtr_tmp
    )
}

fun List<*>.toRibbonData(gruName: String):Map<String,List<Any>> {
    val gru_nam = mutableListOf<String>()
    val sta_cde = mutableListOf<String>()
    val sta_nam_kor = mutableListOf<String>()
    val obs_datetime = mutableListOf<String>()
    val tmp_min = mutableListOf<Float>()
    val tmp_max = mutableListOf<Float>()
    val tmp_avg = mutableListOf<Float>()

    this.forEach {
        if (it is SeaWaterInfoByOneHourStat && it.gru_nam.equals(gruName)) {
            gru_nam.add(it.gru_nam)
            sta_cde.add(it.sta_cde)
            sta_nam_kor.add(it.sta_nam_kor)
            obs_datetime.add(it.obs_datetime)
            tmp_min.add(it.tmp_min.toFloat())
            tmp_max.add(it.tmp_max.toFloat())
            tmp_avg.add(it.tmp_avg.toFloat())
        }
    }

    return mapOf<String, List<Any>> (
        "GroupName" to gru_nam,
        "ObservatoryName" to sta_nam_kor,
        "ObservatoryCode" to sta_cde,
        "CollectingTime" to obs_datetime,
        "TemperatureMin" to tmp_min,
        "TemperatureMax" to tmp_max,
        "TemperatureAvg" to tmp_avg,
    )

}


fun SeawaterInformationByObservationPoint.toList(): List<Any?> {
    val convertList = mutableListOf<Any?>()
    convertList.add(this.obs_datetime)
    convertList.add(this.gru_nam)
    convertList.add(this.sta_nam_kor)
    convertList.add(this.sta_cde)
    convertList.add(
        when(this.obs_lay) {
            "1" -> "표층"
            "2" -> "중층"
            "3" -> "저층"
            else -> {""}
        }
    )
    convertList.add(this.wtr_tmp.toDouble())
    convertList.add(this.lon)
    convertList.add(this.lat)

    return convertList
}

