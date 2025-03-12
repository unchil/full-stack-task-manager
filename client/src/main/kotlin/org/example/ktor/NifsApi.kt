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
import org.jetbrains.kotlinx.dataframe.api.*

class NifsApi {

    val Config = DataRow.readJson(path="/Volumes/WorkSpace/Dev/full-stack-task-manager/client/src/main/resources/application.json")
    val NIFS_API by columnGroup()
    val endPoint by NIFS_API.column<String>()
    val apikey by NIFS_API.column<String>()
    val subPath by NIFS_API.column<String>()
    val id by NIFS_API.columnGroup()
    val list by id.column<String>()
    val code by id.column<String>()

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
        client.get(urlString = Config[endPoint] ){
            url{
                appendPathSegments( Config[subPath])
                parameters.append("id", Config[list] )
                parameters.append("key", Config[apikey] )
            }
        }.let {
            return it.bodyAsText(java.nio.charset.Charset.forName("EUC-KR"))
        }
    }

    suspend fun getObservatory():String {
        client.get(urlString = Config[endPoint] ){
            url{
                appendPathSegments(Config[subPath])
                parameters.append("id", Config[code])
                parameters.append("key", Config[apikey])
            }
        }.let {
            return it.bodyAsText(java.nio.charset.Charset.forName("EUC-KR"))
        }
    }

}