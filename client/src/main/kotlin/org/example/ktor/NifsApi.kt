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

class NifsApi {

    companion object {

        suspend fun callOpenAPI_json(id:String):String{
            client.get(urlString = Config.Item[Config.endPoint] ){
                url{
                    appendPathSegments( Config.Item[Config.subPath])
                    parameters.append("id", Config.Item[ if (id.equals("list")) Config.list else Config.code ] )
                    parameters.append("key", Config.Item[Config.apikey] )
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