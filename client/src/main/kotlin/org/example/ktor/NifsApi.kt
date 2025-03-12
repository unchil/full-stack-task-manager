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
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.io.readJson


class NifsApi {
    val Config = DataRow.readJson(path="/Volumes/WorkSpace/Dev/full-stack-task-manager/client/src/main/resources/application.json")

    val endPoint = Config["endPoint"].toString()
    val apiKey = Config["key"].toString()

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
                appendPathSegments( Config["subPath"].toString())
                parameters.append("id", Config["list"].toString())
                parameters.append("key", apiKey)
            }
        }.let {
            return it.bodyAsText(java.nio.charset.Charset.forName("EUC-KR"))
        }
    }

    suspend fun getObservatory():String {
        client.get(urlString = endPoint ){
            url{
                appendPathSegments(Config["subPath"].toString())
                parameters.append("id", Config["code"].toString())
                parameters.append("key", apiKey)
            }
        }.let {
            return it.bodyAsText(java.nio.charset.Charset.forName("EUC-KR"))
        }
    }

}