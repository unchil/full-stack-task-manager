package org.example.ktor

import org.jetbrains.exposed.sql.Table
import kotlin.math.round

object ObservationTable: Table("Observation"){
    val sta_cde = varchar("sta_cde", 5)
    val sta_nam_kor = varchar("sta_nam_kor", 50)
    val obs_dat = varchar("obs_dat", 10)
    val obs_tim = varchar("obs_tim", 8)
    val repair_gbn =  integer("repair_gbn")
    val obs_lay = integer("obs_lay")
    val wtr_tmp = float("wtr_tmp")
    val dox = float("dox")
    val sal = float("sal")

    override val primaryKey = PrimaryKey(sta_cde, obs_dat, obs_tim, obs_lay, name = "primaryKey")
}


object ObservatoryTable: Table("Observatory"){
    val sta_cde = varchar("sta_cde", 5)
    val sta_nam_kor = varchar("sta_nam_kor", 50)
    val bld_dat = varchar("bld_dat", 10)
    val end_dat = varchar("end_dat", 10)
    val gru_nam =  varchar("gru_nam", 30)
    val lon = double("lon")
    val lat = double("lat")
    val sur_tmp_yn = varchar("sur_tmp_yn", 1)
    val mid_tmp_yn = varchar("mid_tmp_yn", 1)
    val bot_tmp_yn = varchar("bot_tmp_yn", 1)
    val sur_dep = float("sur_dep")
    val mid_dep = float("mid_dep")
    val bot_dep = float("bot_dep")
    val sta_des = varchar("sta_des", 250)

    override val primaryKey = PrimaryKey(sta_cde, name = "primaryKey")
}

