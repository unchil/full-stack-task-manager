package org.example.ktor

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.io.readJson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import io.ktor.serialization.kotlinx.xml.*

class NifsApi {

       companion object {

           val config_df = DataRow.readJson(path= this::class.java.classLoader?.getResource("application.json")!!.path)

           suspend fun callNifsAPI_json(id:String):String{

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

           suspend fun callMofAPI_xml():OceanWaterResponse {

               val now = Clock.System.now()
               @OptIn(FormatStringsInDatetimeFormats::class)
               val currentTime = now
                   .toLocalDateTime(TimeZone.of("Asia/Seoul"))
                   .format(LocalDateTime.Format{byUnicodePattern("yyyy-MM-dd HH:mm:ss")})

               @OptIn(FormatStringsInDatetimeFormats::class)
               val previous2Hour = now
                   .minus(2, DateTimeUnit.HOUR)
                   .toLocalDateTime(TimeZone.of("Asia/Seoul"))
                   .format(LocalDateTime.Format{byUnicodePattern("yyyy-MM-dd HH:mm:ss")})

               LOGGER.debug("Current time : ${currentTime}, Previous time : ${previous2Hour}")

               val confData = this.config_df["MOF_API"] as DataRow<*>

               val url = "${confData["endPoint"]}/${confData["subPath"]}" +
                       "?wtch_dt_start=${URLEncoder.encode(previous2Hour, StandardCharsets.UTF_8.toString())}" +
                       "&numOfRows=1000" +
                       "&pageNo=1" +
                       "&ServiceKey=${confData["apikey"]}"

               client.get(urlString = url).let {
                   return it.body<OceanWaterResponse>()
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