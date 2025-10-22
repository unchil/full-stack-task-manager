package org.example.ktor

import kotlinx.serialization.Serializable
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema

@Serializable
data class Observation(
    val sta_cde: String,
    val sta_nam_kor: String,
    val obs_dat: String,
    val obs_tim: String,
    val repair_gbn: String,
    val obs_lay: String,
    val wtr_tmp: String?,
)

@DataSchema
interface Observation2 {
    val sta_cde: String
    val sta_nam_kor: String
    val obs_dat: String
    val obs_tim: String
    val repair_gbn: String
    val obs_lay: String
    val wtr_tmp: String?
    val dox: String?
    val sal: String?
}

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
data class Header(
    val resultCode: String,
    val resultMsg: String
)


@Serializable
data class OceanWaterQuality (
    val num: String, // 순번
    val rtmWqWtchStaCd: String, // 실시간수질관측정점코드
    val rtmWqWtchDtlDt: String, // 실시간수질관측상세일시
    val rtmWtchWtem: String, // 실시간관측수온
    val rtmWqCndctv: String, // 실시간수질전기전도도
    val ph: String, // 수소이온농도
    val rtmWqDoxn: String, // 실시간수질용존산소량
    val rtmWqTu: String, // 실시간수질탁도
    val rtmWqBgalgsQy: String?, // 실시간수질남조류량
    val rtmWqChpla: String, // 실시간수질클로로필
    val rtmWqSlnty: String // 실시간수질염분
)



@Serializable
data class OceanWaterQualityBody(
    val items: List<OceanWaterQuality>,
    val numOfRows: String,
    val pageNo: String,
    val totalCount: String
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

@Serializable
data class OceanWaterResponse(
    val header: Header,
    val body: OceanWaterQualityBody
)