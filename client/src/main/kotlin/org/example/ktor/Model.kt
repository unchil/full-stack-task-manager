package org.example.ktor

import kotlinx.serialization.Serializable


@Serializable
data class Observation(
    val sta_cde: String,
    val sta_nam_kor: String,
    val obs_dat: String,
    val obs_tim: String,
    val repair_gbn: Int,
    val obs_lay: Int,
    val wtr_tmp: Double,
    val dox: Double?,
    val sal: Double?,
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
    val sur_dep: Double?,
    val mid_dep: Double?,
    val bot_dep: Double?,
    val sta_des: String?
)

@Serializable
data class Header(
    val resultCode: String,
    val resultMsg: String
)

@Serializable
data class ObservationBody(
    val item: List<Observation>,
)

@Serializable
data class  ObservatoryBody(
    val item: List<Observatory>
)

@Serializable
data class ObservationResponse(
    val header: Header,
    val body: ObservationBody
)

@Serializable
data class ObservatoryResponse(
    val header: Header,
    val body: ObservatoryBody
)