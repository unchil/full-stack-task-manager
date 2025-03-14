package org.example.ktor

import io.ktor.client.plugins.logging.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class Repository {

    private val logger = Logger.DEFAULT

    init {
        transaction(conn) {
            addLogger(StdOutSqlLogger)
        }
    }

    @Suppress("DefaultLocale")
    suspend fun getRealTimeObservation(){
        try{
            NifsApi.callOpenAPI_json("list").let {
                val recvData = Json.decodeFromString<ObservationResponse>(it)
                if(recvData.header.resultCode.equals("00")){
                    logger.log(::getRealTimeObservation.name  + ": receive count [" + recvData.body.item.size + "]"  )

                    transaction (conn){

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
                                    it[wtr_tmp] = String.format("%.2f", item.wtr_tmp)
                                    it[dox] = if(item.dox != null)  String.format("%.2f", item.dox) else "0.0"
                                    it[sal] = if(item.sal != null)  String.format("%.2f", item.sal) else "0.0"
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

    @Suppress("DefaultLocale")
    suspend fun getRealTimeObservatory(){
        try{
            NifsApi.callOpenAPI_json("code").let {
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
                                it[sur_dep] =  if(item.sur_dep != null) String.format("%.1f", item.sur_dep) else "0.0"
                                it[mid_dep] =  if(item.mid_dep != null) String.format("%.1f", item.mid_dep) else "0.0"
                                it[bot_dep] =  if(item.bot_dep != null) String.format("%.1f", item.bot_dep) else "0.0"
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

    companion object {
        val conn = Database.connect(
            url = Config.Item[Config.jdbcURL],
            driver =  Config.Item[Config.driverClassName]
        )

    }

}