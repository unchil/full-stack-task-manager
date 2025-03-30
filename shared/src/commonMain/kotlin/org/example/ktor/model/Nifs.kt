package org.example.ktor.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Observation(
    val sta_cde: String,
    val sta_nam_kor: String,
    val obs_datetime: String,
    val repair_gbn: String,
    val obs_lay: String,
    val wtr_tmp: String,
    val dox: String?,
    val sal: String?,
)

@Serializable
data class Observatory(
    val sta_cde: String,
    val sta_nam_kor: String,
    val bld_dat: String,
    val end_dat: String?,
    val gru_nam: String,
    val lon: Double,
    val lat: Double,
    val sur_tmp_yn: String,
    val mid_tmp_yn: String,
    val bot_tmp_yn: String,
    val sur_dep: String?,
    val mid_dep: String?,
    val bot_dep: String?,
    val sta_des: String?
)


@Serializable
data class SeawaterInformationByObservationPoint(
    val sta_cde: String,
    val sta_nam_kor: String,
    val obs_datetime: String,
    val obs_lay: String,
    val wtr_tmp: String,
    val dox: String?,
    val sal: String?,
    val gru_nam: String,
    val lon: Double,
    val lat: Double,
)

@Serializable
data class SeaWaterInfoByOneHourStat(
    val gru_nam: String,
    val sta_cde: String,
    val sta_nam_kor: String,
    val obs_datetime: String,
    val tmp_min: String,
    val tmp_max: String,
    val tmp_avg: String
)




@Serializable
data class GeoJson(
    val type: String,
    val features: List<Feature>? = null,
    val geometry: Geometry? = null,
    val coordinates: List<JsonElement>? = null,
    val geometries: List<Geometry>? = null
)

@Serializable
data class Feature(
    val type: String,
    val geometry: Geometry,
    val properties: Map<String, JsonElement>? = null
)

@Serializable
data class Geometry(
    val type: String,
    val coordinates: List<JsonElement>? = null,
    val geometries: List<Geometry>? = null
)