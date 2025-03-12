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
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnGroup
import org.jetbrains.kotlinx.dataframe.io.readJson

class NifsApi {

    companion object {
        private val NIFS_API by columnGroup()
        private val endPoint by NIFS_API.column<String>()
        private val apikey by NIFS_API.column<String>()
        private val subPath by NIFS_API.column<String>()
        private val id by NIFS_API.columnGroup()
        private val list by id.column<String>()
        private val code by id.column<String>()
        private val Config = DataRow.readJson(path=this::class.java.classLoader.getResource("application.json")!!.path)

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
    }
}