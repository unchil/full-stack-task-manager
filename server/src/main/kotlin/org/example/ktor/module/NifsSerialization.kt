package org.example.ktor.module


import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.ktor.data.NifsRepository
import org.example.ktor.model.SeaWaterInfoByOneHourStat
import org.example.ktor.model.SeawaterInformationByObservationPoint
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

// 캐시를 저장할 ConcurrentHashMap. 스레드 안전성을 보장합니다.
private val cache_SeawaterInfo = ConcurrentHashMap<String, Pair<List<SeawaterInformationByObservationPoint>, Long>>()
private val cache_SeaWaterInfoStatistics = ConcurrentHashMap<String, Pair<List<SeaWaterInfoByOneHourStat>, Long>>()
private const val CACHE_EXPIRY_SECONDS =  10 * 60L  // 10분


fun Application.configureNifsSerialization(repository: NifsRepository) {

    install(ContentNegotiation) {
        json()
    }

    install(DefaultHeaders){
        header("Access-Control-Allow-Origin", "*")
    }

    routing{
        get("/") {
            call.respondText("Beautiful World!")
        }

        route("/nifs") {

            get("/seawaterinfo/{division}"){

                val division = call.parameters["division"]
                if (division == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                val key_SeawaterInfo = "cache_$division"
                val now = System.currentTimeMillis()

                // 1. 캐시에서 데이터 조회
                cache_SeawaterInfo[key_SeawaterInfo]?.let { it ->
                    if( (now - it.second) < TimeUnit.SECONDS.toMillis(CACHE_EXPIRY_SECONDS) ){
                // 2. 캐시가 유효하면 즉시 응답
                        println("Serving from cache for ID: $division")
                        return@get call.respond(it.first)
                    }
                }
                // 3. 캐시가 없거나 만료되면 DB에서 데이터 조회
                try {
                    val result = repository.seaWaterInfo(division)
                    if (result.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                // 4. 새로운 데이터를 캐시에 저장
                    cache_SeawaterInfo[key_SeawaterInfo] = Pair(result, now)
                // 5. 응답
                    call.respond(result)
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            get ("/stat"){
                val key_SeaWaterInfoStatistics = "cache_stat"
                val now = System.currentTimeMillis()
                cache_SeaWaterInfoStatistics[key_SeaWaterInfoStatistics]?.let {
                    if( (now - it.second) < TimeUnit.SECONDS.toMillis(CACHE_EXPIRY_SECONDS) ){
                        println("Serving from cache for ID: stat")
                        return@get call.respond(it.first)
                    }
                }
                try {
                    val result = repository.seaWaterInfoStatistics()
                    if (result.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    cache_SeaWaterInfoStatistics[key_SeaWaterInfoStatistics] = Pair(result, now)
                    call.respond(result)
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }

            }

            get("/observations/{division}"){
                val division = call.parameters["division"]
                if (division == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                try {
                    val result = repository.observationList(division)
                    if (result.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    call.respond(result)

                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }

            }

            get ("/observatory"){
                try {
                    val result = repository.observatoryInfo()
                    if (result.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    call.respond(result)

                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }

            }

        }

    }

}