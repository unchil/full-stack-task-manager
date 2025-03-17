package org.example.ktor

import org.jetbrains.exposed.sql.Table

object ObservationTable: Table("Observation"){
    val sta_cde = varchar("sta_cde", 5)
    val sta_nam_kor = varchar("sta_nam_kor", 50)
    val obs_dat = varchar("obs_dat", 10)
    val obs_tim = varchar("obs_tim", 8)
    val obs_datetime = varchar("obs_datetime", 19)
    val repair_gbn =  varchar("repair_gbn", 1)
    val obs_lay = varchar("obs_lay", 1)
    val wtr_tmp = varchar("wtr_tmp", 10)
    val dox = varchar("dox", 10).nullable()
    val sal = varchar("sal", 10).nullable()

    init {
        index("idx_datetime", false, columns = arrayOf(obs_datetime) )
    }

    override val primaryKey = PrimaryKey(sta_cde, obs_dat, obs_tim, obs_lay, name = "primaryKey")
}


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

