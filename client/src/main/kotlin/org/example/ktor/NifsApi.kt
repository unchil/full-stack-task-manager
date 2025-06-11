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
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.io.readJson

class NifsApi {

       companion object {

           val config_df = DataRow.readJson(path= this::class.java.classLoader?.getResource("application.json")!!.path)

           suspend fun callOpenAPI_json(id:String):String{

                val confData = this.config_df["NIFS_API"] as DataRow<*>

                val url = confData["endPoint"].toString()
                val serviceID =  if (id.equals("list")) {
                    (confData["id"] as DataRow<*>)["list"].toString()
                } else{
                    (confData["id"] as DataRow<*>)["code"].toString()
                }
                val key = confData["apikey"].toString()
                val subPath = confData["subPath"].toString()

                client.get(urlString =  url) {
                    url{
                        appendPathSegments( subPath)
                        parameters.append("id", serviceID )
                        parameters.append("key", key )
                    }
                }.let {
                    return it.bodyAsText(java.nio.charset.Charset.forName("EUC-KR"))
                }
        }

        val client = HttpClient(CIO) {

            install(Logging){
                logger = Logger.DEFAULT
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