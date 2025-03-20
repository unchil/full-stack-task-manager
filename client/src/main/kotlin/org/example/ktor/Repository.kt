package org.example.ktor

import io.ktor.client.plugins.logging.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import io.ktor.util.logging.*

class Repository {

    internal val LOGGER = KtorSimpleLogger("org.example.ktor")

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
                    LOGGER.debug(::getRealTimeObservation.name  + ": receive count [" + recvData.body.item.size + "]")
                    transaction (conn){

                        SchemaUtils.create( ObservationTable)

                        recvData.body.item.forEach { item ->

                            try{
                                ObservationTable.insert { it->
                                    it[sta_cde] = item.sta_cde
                                    it[sta_nam_kor] = item.sta_nam_kor
                                    it[obs_dat] = item.obs_dat
                                    it[obs_tim] = item.obs_tim
                                    it[obs_datetime] = "${item.obs_dat} ${item.obs_tim}"
                                    it[repair_gbn] = item.repair_gbn
                                    it[obs_lay] = item.obs_lay
                                    it[wtr_tmp] = item.wtr_tmp
                                    it[dox] = item.dox
                                    it[sal] = item.sal
                                }
                            } catch (e:Exception){
                                LOGGER.debug("Exception PRIMARYKEY: [" + item.sta_cde + "," + item.obs_dat + "," + item.obs_tim + "," + item.obs_lay + "]")
                                e.localizedMessage?.let {
                                    LOGGER.error(it)
                                }
                            }

                        }
                    }
                }else{
                    LOGGER.debug(::getRealTimeObservation.name  +  ": receive message [" + recvData.header.resultMsg + "]" )
                }
            }
        } catch(e: Exception) {
            e.localizedMessage?.let {
                LOGGER.error(it)
            }
        }
    }

    @Suppress("DefaultLocale")
    suspend fun getRealTimeObservatory(){
        try{
            NifsApi.callOpenAPI_json("code").let {
                val recvData = Json.decodeFromString<ObservatoryResponse>(it)
                if(recvData.header.resultCode.equals("00")) {

                    LOGGER.debug(::getRealTimeObservatory.name  + ": receive count [" + recvData.body.item.size + "]"  )

                    transaction (conn){
                        SchemaUtils.drop( ObservatoryTable)
                        SchemaUtils.create( ObservatoryTable)
                        recvData.body.item.forEach { item ->
                            ObservatoryTable.insert { it ->
                                it[sta_cde] = item.sta_cde
                                it[sta_nam_kor] = item.sta_nam_kor
                                it[bld_dat] = item.bld_dat
                                it[end_dat] = item.end_dat
                                it[gru_nam] = item.gru_nam
                                it[lon] = item.lon
                                it[lat] = item.lat
                                it[sur_tmp_yn] = item.sur_tmp_yn
                                it[mid_tmp_yn] = item.mid_tmp_yn
                                it[bot_tmp_yn] = item.bot_tmp_yn
                                it[sur_dep] =  item.sur_dep
                                it[mid_dep] =  item.mid_dep
                                it[bot_dep] =  item.bot_dep
                                it[sta_des] = item.sta_des
                            }
                        }
                    }

                }else{
                    LOGGER.debug(::getRealTimeObservatory.name  +  ": receive message [" + recvData.header.resultMsg + "]" )
                }
            }
        } catch (e: Exception){
            e.localizedMessage?.let {
                LOGGER.error(it)
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