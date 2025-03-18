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
import org.example.ktor.model.Observation
import org.example.ktor.model.Observatory

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
            logger = Logger.DEFAULT
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 10000
            connectTimeoutMillis = 3000
            socketTimeoutMillis = 3000
        }
    }

    suspend fun getObservation(division:String): List<Observation> {
        val url = "${endPoint}/nifs/observations/$division"
        return httpClient.get(url).body()
    }

    suspend fun getObservatory(): List<Observatory> {
        val url = "${endPoint}/nifs/observatory"
        return httpClient.get(url).body()
    }

}