package org.example.ktor.db.entity

import org.example.ktor.model.Observation
import org.example.ktor.model.Observatory
import org.example.ktor.model.SeawaterInformationByObservationPoint
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table


fun toSeawaterInformationByObservationPoint(it: ResultRow) = SeawaterInformationByObservationPoint(

    it[ObservationTable.sta_cde],
    it[ObservationTable.sta_nam_kor],
    it[ObservationTable.obs_datetime],
    it[ObservationTable.obs_lay],
    it[ObservationTable.wtr_tmp],
    it[ObservatoryTable.gru_nam],
    it[ObservatoryTable.lon],
    it[ObservatoryTable.lat]

)


object ObservationTable: Table("Observation"){
    val sta_cde = varchar("sta_cde", 5)
    val sta_nam_kor = varchar("sta_nam_kor", 50)
    val obs_dat = varchar("obs_dat", 10)
    val obs_tim = varchar("obs_tim", 8)
    val obs_datetime = varchar("obs_datetime", 19)
    val repair_gbn =  varchar("repair_gbn", 1)
    val obs_lay = varchar("obs_lay", 1)
    val wtr_tmp = varchar("wtr_tmp", 10)

    init {
        index("idx_datetime", false, columns = arrayOf(obs_datetime) )
    }

    override val primaryKey = PrimaryKey(sta_cde, obs_dat, obs_tim, obs_lay, name = "primaryKey")
}

fun toObservation(it: ResultRow) = Observation (
    it[ObservationTable.sta_cde],
    it[ObservationTable.sta_nam_kor],
    it[ObservationTable.obs_datetime],
    it[ObservationTable.repair_gbn],
    it[ObservationTable.obs_lay],
    it[ObservationTable.wtr_tmp]
)


object ObservatoryTable: Table("Observatory"){
    val sta_cde = varchar("sta_cde", 5)
    val sta_nam_kor = varchar("sta_nam_kor", 30)
    val bld_dat = varchar("bld_dat", 10)
    val end_dat = varchar("end_dat", 10).nullable()
    val gru_nam =  varchar("gru_nam", 30)
    val lon = double("lon")
    val lat = double("lat")
    val sur_tmp_yn = varchar("sur_tmp_yn", 1)
    val mid_tmp_yn = varchar("mid_tmp_yn", 1)
    val bot_tmp_yn = varchar("bot_tmp_yn", 1)
    val sur_dep = varchar("sur_dep", 10).nullable()
    val mid_dep = varchar("mid_dep", 10).nullable()
    val bot_dep = varchar("bot_dep", 10).nullable()
    val sta_des = varchar("sta_des", 250).nullable()

    override val primaryKey = PrimaryKey(sta_cde, name = "primaryKey")
}

fun toObservatory(it: ResultRow) = Observatory (
    it[ObservatoryTable.sta_cde],
    it[ObservatoryTable.sta_nam_kor],
    it[ObservatoryTable.bld_dat],
    it[ObservatoryTable.end_dat],
    it[ObservatoryTable.gru_nam],
    it[ObservatoryTable.lon],
    it[ObservatoryTable.lat],
    it[ObservatoryTable.sur_tmp_yn],
    it[ObservatoryTable.mid_tmp_yn],
    it[ObservatoryTable.bot_tmp_yn],
    it[ObservatoryTable.sur_dep],
    it[ObservatoryTable.mid_dep],
    it[ObservatoryTable.bot_dep],
    it[ObservatoryTable.sta_des]
)

