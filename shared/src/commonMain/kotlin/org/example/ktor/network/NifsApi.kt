package org.example.ktor.network


import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.example.ktor.getPlatform
import org.example.ktor.model.Observatory
import org.example.ktor.model.SeaWaterInfoByOneHourStat
import org.example.ktor.model.SeawaterInformationByObservationPoint

class NifsApi {

    private val endPoint = "http://${if( getPlatform().name.contains("Android") ) "10.0.2.2" else "localhost"}:7788"

    private val httpClient = HttpClient() {

        install(ContentNegotiation) {
            json(Json {
                encodeDefaults = true
                isLenient = true
                coerceInputValues = true
                ignoreUnknownKeys = true
            })
        }

        install(Logging) {
            logger = Logger.EMPTY
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 10 * 1000
            connectTimeoutMillis = 10 * 1000
            socketTimeoutMillis = 10 * 1000
        }
    }

    suspend fun getSeaWaterInfo(division:String): List<SeawaterInformationByObservationPoint> {
        val url = "${endPoint}/nifs/seawaterinfo/$division"
        val result = httpClient.get(url).body<List<SeawaterInformationByObservationPoint>>()
        return result
    }


    suspend fun getSeaWaterInfoStat(): List<SeaWaterInfoByOneHourStat> {
        val url = "${endPoint}/nifs/stat"

        val result = httpClient.get(url).body<List<SeaWaterInfoByOneHourStat>>()
        return result

    }

    suspend fun getObservatory(): List<Observatory> {
        val url = "${endPoint}/nifs/observatory"

            val result = httpClient.get(url).body<List<Observatory>>()
            return result

    }

}