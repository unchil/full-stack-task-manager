package org.example.ktor

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


class NifsApi {

    val endPoint = "https://www.nifs.go.kr"
    val apiKey = "*************************"

    val client = HttpClient(CIO) {
/*
        install(Logging){
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }

 */
        install(ContentNegotiation) {
            json(Json {
                encodeDefaults = true
                isLenient = true
                coerceInputValues = true
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 10000
            connectTimeoutMillis = 3000
            socketTimeoutMillis = 3000
        }
    }


    suspend fun getObservation():String {
        client.get(urlString = endPoint ){
            url{
                appendPathSegments( "OpenAPI_json")
                parameters.append("id", "risaList")
                parameters.append("key", apiKey)
            }
        }.let {
            return it.bodyAsText(java.nio.charset.Charset.forName("EUC-KR"))
        }
    }

    suspend fun getObservatory():String {
        client.get(urlString = endPoint ){
            url{
                appendPathSegments( "OpenAPI_json")
                parameters.append("id", "risaCode")
                parameters.append("key", apiKey)
            }
        }.let {
            return it.bodyAsText(java.nio.charset.Charset.forName("EUC-KR"))
        }
    }

}