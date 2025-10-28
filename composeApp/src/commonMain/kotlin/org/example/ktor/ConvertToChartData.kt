package org.example.ktor

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import org.example.ktor.model.SeaWaterInfoByOneHourStat
import org.example.ktor.model.SeaWaterInformation
import org.example.ktor.model.SeawaterInformationByObservationPoint
import kotlin.time.Duration.Companion.hours


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

@OptIn(FormatStringsInDatetimeFormats::class)
fun List<*>.toMofLineData(qualityType: WATER_QUALITY.QualityType):Map<String,List<Any>> {
    val rtmWqWtchStaName = mutableListOf<String>()
    val value = mutableListOf<Float>()
    val rtmWqWtchDtlDt = mutableListOf<Any>()

    val dateTimeFormatInput = LocalDateTime.Format { byUnicodePattern("yyyy-MM-dd HH:mm:ss") }

    this.forEach {
        if (it is SeaWaterInformation &&  !listOf("SEA6001","SEA1005" ).contains(it.rtmWqWtchStaCd)) {
            rtmWqWtchStaName.add(it.rtmWqWtchStaName)
            rtmWqWtchDtlDt.add(
                 LocalDateTime.parse(it.rtmWqWtchDtlDt, dateTimeFormatInput)
                     .toInstant(TimeZone.UTC)
                     .toEpochMilliseconds()
            )


            when(qualityType){
                WATER_QUALITY.QualityType.rtmWtchWtem -> {
                    if (it.rtmWtchWtem.trim().toFloat() < 0.0){
                        value.add( 0f)
                    }else{
                        value.add(it.rtmWtchWtem.trim().toFloat())
                    }
                }
                WATER_QUALITY.QualityType.rtmWqCndctv -> {
                    if (it.rtmWqCndctv.trim().toFloat() < 10.0){
                        value.add( 10f)
                    }else{
                        value.add(it.rtmWqCndctv.trim().toFloat())
                    }
                }
                WATER_QUALITY.QualityType.ph -> {

                    if (it.ph.trim().toFloat() < 7.0){
                        value.add( 7f)
                    }else{
                        value.add(it.ph.trim().toFloat())
                    }
                }
                WATER_QUALITY.QualityType.rtmWqDoxn -> {
                    if (it.rtmWqDoxn.trim().toFloat() < 0.0){
                        value.add( 0f)
                    }else{
                        value.add( it.rtmWqDoxn.trim().toFloat() )
                    }
                }
                WATER_QUALITY.QualityType.rtmWqTu -> {
                    if (it.rtmWqTu.trim().toFloat() < 0.0){
                        value.add( 0f)
                    }else{
                        value.add( it.rtmWqTu.trim().toFloat() )
                    }
                }
                WATER_QUALITY.QualityType.rtmWqChpla -> {
                    if (it.rtmWqChpla.trim().toFloat() < 0.0){
                        value.add( 0f)
                    }else{
                        value.add( it.rtmWqChpla.trim().toFloat() )
                    }

                }
                WATER_QUALITY.QualityType.rtmWqSlnty -> {
                    if (it.rtmWqSlnty.trim().toFloat() < 6.0){
                        value.add( 6f)
                    }else{
                        value.add(it.rtmWqSlnty.trim().toFloat())
                    }
                }
            }
        }
    }

    val data = mapOf<String, List<Any>>(
        "CollectingTime" to rtmWqWtchDtlDt,
        "ObservatoryName" to rtmWqWtchStaName,
        "Value" to value
    )
    return data

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

