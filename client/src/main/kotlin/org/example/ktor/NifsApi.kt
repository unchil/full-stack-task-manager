package org.example.ktor

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.api.*

class NifsApi {

    val NIFS_API by columnGroup()
    val endPoint by NIFS_API.column<String>()
    val apikey by NIFS_API.column<String>()
    val subPath by NIFS_API.column<String>()
    val id by NIFS_API.columnGroup()
    val list by id.column<String>()
    val code by id.column<String>()

    val Config: AnyRow

    init {
        val confPath = this::class.java.classLoader.getResource("application.json")!!.path
        Config = DataRow.readJson(path=confPath)
    }

    val client = HttpClient(CIO) {

        install(Logging){
            logger = Logger.SIMPLE
            level = LogLevel.INFO
        }

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

    suspend fun callOpenAPI_json(id:String):String{

        client.get(urlString = Config[endPoint] ){
            url{
                appendPathSegments( Config[subPath])
                parameters.append("id", Config[ if (id.equals("list")) list else code ] )
                parameters.append("key", Config[apikey] )
            }
        }.let {
            return it.bodyAsText(java.nio.charset.Charset.forName("EUC-KR"))
        }
    }


}