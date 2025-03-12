package org.example.ktor

import io.ktor.client.plugins.logging.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.api.*

class Repository {

    val logger = Logger.DEFAULT

    private val conn:Database
    private val nifsApi:NifsApi

    init {
        val SQLITE_DB by columnGroup()
        val driverClassName  by SQLITE_DB.column<String>()
        val jdbcURL by SQLITE_DB.column<String>()

        val confPath = this::class.java.classLoader.getResource("application.json")!!.path
        val Config = DataRow.readJson(path=confPath)

        conn = Database.connect(
            url = Config[jdbcURL],
            driver =  Config[driverClassName]
        )
        transaction(conn) {
            addLogger(StdOutSqlLogger)
        }

        nifsApi = NifsApi()
    }


    suspend fun getRealTimeObservation(){
        try{
            nifsApi.getObservation().let {
                val recvData = Json.decodeFromString<ObservationResponse>(it)
                if(recvData.header.resultCode.equals("00")){
                    logger.log(::getRealTimeObservation.name  + ": receive count [" + recvData.body.item.size + "]"  )

                    transaction (conn){
                 //       SchemaUtils.drop( ObservationTable)
                        SchemaUtils.create( ObservationTable)

                        recvData.body.item.forEach { item ->

                            try{
                                ObservationTable.insert { it->
                                    it[sta_cde] = item.sta_cde
                                    it[sta_nam_kor] = item.sta_nam_kor
                                    it[obs_dat] = item.obs_dat
                                    it[obs_tim] = item.obs_tim
                                    it[repair_gbn] = item.repair_gbn
                                    it[obs_lay] = item.obs_lay
                                    it[wtr_tmp] = item.wtr_tmp
                                    it[dox] = item.dox ?: "0.0".toFloat()
                                    it[sal] = item.sal ?: "0.0".toFloat()
                                }
                            } catch (e:Exception){
                                logger.log("Exception PRIMARYKEY: [" + item.sta_cde + "," + item.obs_dat + "," + item.obs_tim + "," + item.obs_lay + "]")
                                e.localizedMessage?.let {
                                    logger.log(it)
                                }
                            }

                        }
                    }
                }else{
                    logger.log(::getRealTimeObservation.name  +  ": receive message [" + recvData.header.resultMsg + "]" )
                }
            }
        } catch(e: Exception) {
            e.localizedMessage?.let {
                logger.log(it)
            }
        }
    }

    suspend fun getRealTimeObservatory(){
        try{
            nifsApi.getObservatory().let {
                val recvData = Json.decodeFromString<ObservatoryResponse>(it)
                if(recvData.header.resultCode.equals("00")) {

                    logger.log(::getRealTimeObservatory.name  + ": receive count [" + recvData.body.item.size + "]"  )

                    transaction (conn){
                        SchemaUtils.drop( ObservatoryTable)
                        SchemaUtils.create( ObservatoryTable)
                        recvData.body.item.forEach { item ->
                            ObservatoryTable.insert { it ->
                                it[sta_cde] = item.sta_cde
                                it[sta_nam_kor] = item.sta_nam_kor
                                it[bld_dat] = item.bld_dat
                                it[end_dat] = item.end_dat ?: ""
                                it[gru_nam] = item.gru_nam
                                it[lon] = item.lon
                                it[lat] = item.lat
                                it[sur_tmp_yn] = item.sur_tmp_yn
                                it[mid_tmp_yn] = item.mid_tmp_yn
                                it[bot_tmp_yn] = item.bot_tmp_yn
                                it[sur_dep] = item.sur_dep ?: "0.0".toFloat()
                                it[mid_dep] = item.mid_dep ?: "0.0".toFloat()
                                it[bot_dep] = item.bot_dep ?: "0.0".toFloat()
                                it[sta_des] = item.sta_des ?: ""
                            }
                        }
                    }

                }else{
                    logger.log(::getRealTimeObservatory.name  +  ": receive message [" + recvData.header.resultMsg + "]" )
                }
            }
        } catch (e: Exception){
            e.localizedMessage?.let {
                logger.log(it)
            }
        }
    }


}