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
import org.example.ktor.Config.Companion.configData

class NifsApi {

       companion object {
           suspend fun callNifsAPI_json(id:String):String{
                client.get(urlString =  configData.NIFS_API?.endPoint ?: "") {
                    url{
                        appendPathSegments( configData.NIFS_API?.subPath ?: "")
                        parameters.append("id",  if (id.equals("list")) {
                            configData.NIFS_API?.id?.list ?: ""
                        } else{
                            configData.NIFS_API?.id?.code ?: ""
                        })
                        parameters.append("key", configData.NIFS_API?.apikey ?: "" )
                    }
                }.let {
                    return it.bodyAsText(java.nio.charset.Charset.forName("EUC-KR"))
                }
           }

           suspend fun callMofAPI_xml():HttpResponse {
               val now = Clock.System.now()
               @OptIn(FormatStringsInDatetimeFormats::class)
               val currentTime = now
                   .toLocalDateTime(TimeZone.of("Asia/Seoul"))
                   .format(LocalDateTime.Format{byUnicodePattern("yyyy-MM-dd HH:mm:ss")})

               @OptIn(FormatStringsInDatetimeFormats::class)
               val previous2Hour = now
                   .minus(2, DateTimeUnit.HOUR)
                   .toLocalDateTime(TimeZone.of("Asia/Seoul"))
                   .format(LocalDateTime.Format{byUnicodePattern("yyyy-MM-dd  HH:mm:ss")})

               LOGGER.debug("Current time : ${currentTime}, Previous time : ${previous2Hour}")

               val url = "${configData.MOF_API?.endPoint}/${configData.MOF_API?.subPath}" +
                       "?wtch_dt_start=${URLEncoder.encode(previous2Hour, StandardCharsets.UTF_8.toString())}" +
                       "&wtch_dt_end=${URLEncoder.encode(currentTime, StandardCharsets.UTF_8.toString())}" +
                       "&numOfRows=1000" +
                       "&pageNo=1" +
                       "&ServiceKey=${configData.MOF_API?.apikey}"

               return client.get(urlString = url)
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