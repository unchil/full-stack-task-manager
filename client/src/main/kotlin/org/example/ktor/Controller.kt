package org.example.ktor

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.head
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.io.read
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import org.jetbrains.kotlinx.dataframe.io.toCsvStr
import org.json.XML
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.sql.Connection
import java.sql.DriverManager
import kotlin.use


fun getRealTimeOceanWaterQuailty(jdbcUrl:String){

    val url = makeUrl_RealTimeOceanWaterQuality()
    val pagePath = "$url&pageNo=1"
    val jsonData = XML.toJSONObject(DataFrame.read(pagePath).toCsvStr())
    var df = DataFrame.readJsonStr(jsonData.toString().trimIndent())
    LOGGER.info("\n"+ df.schema().toString())
    df = df.get("response").get("body").get("items").get("item")[0] as DataFrame<*>

    df = df
        .convert{ colsAtAnyDepth().colsOf<Double>() }.with { String.format("%.3f", it) }
        .convert { colsAtAnyDepth().colsOf<Float>() }.with{ String.format("%.3f", it)}
        .convert("rtmWqWtchDtlDt").with { (it as String).substring(0, 19) }

    LOGGER.info("\n"+ df.schema().toString())
    LOGGER.info("\n"+ df.describe().toString())
    LOGGER.info("\n"+ df.head(5).toString())

    val tableName = "OWQInformation"




    try {
        DriverManager.getConnection(jdbcUrl).use {conn ->

            createAndPopulateTable(conn, tableName)

            val sql = """INSERT INTO ${tableName} ( 
                    rtmWqWtchDtlDt, rtmWqWtchStaCd, rtmWtchWtem, rtmWqCndctv,
                    ph, rtmWqDoxn, rtmWqTu, rtmWqBgalgsQy, rtmWqChpla, rtmWqSlnty 
                ) VALUES (?,?,?,?,?,?,?,?,?,? )""".trimIndent()

            LOGGER.info("\n"+ sql)

            df.forEach { it ->
                try {
                    conn.prepareStatement(sql)?.use { preparedStatement ->
                        preparedStatement.setString(1, it["rtmWqWtchDtlDt"].toString())
                        preparedStatement.setString(2, it["rtmWqWtchStaCd"].toString())
                        preparedStatement.setString(3, it["rtmWtchWtem"].toString())
                        preparedStatement.setString(4, it["rtmWqCndctv"].toString())
                        preparedStatement.setString(5, it["ph"].toString())
                        preparedStatement.setString(6, it["rtmWqDoxn"].toString())
                        preparedStatement.setString(7, it["rtmWqTu"].toString())
                        preparedStatement.setString(8, it["rtmWqBgalgsQy"].toString())
                        preparedStatement.setString(9, it["rtmWqChpla"].toString())
                        preparedStatement.setString(10, it["rtmWqSlnty"].toString())

                        preparedStatement.executeUpdate()
                    }
                } catch (e: Exception){
                    LOGGER.warn(e.localizedMessage)
                }
            }

        }

    } catch (e: Exception){
        LOGGER.error(e.localizedMessage)
    }

}

fun createAndPopulateTable(con: Connection, tableName: String) {
    val stmt = con.createStatement()
    val sql = """CREATE TABLE IF NOT EXISTS ${tableName} (
                    rtmWqWtchDtlDt TEXT,
                    rtmWqWtchStaCd TEXT,
                    rtmWtchWtem    TEXT,
                    rtmWqCndctv    TEXT,
                    ph             TEXT,
                    rtmWqDoxn      TEXT,
                    rtmWqTu        TEXT,
                    rtmWqBgalgsQy  TEXT,
                    rtmWqChpla     TEXT,
                    rtmWqSlnty     TEXT,
                    constraint primaryKey
                        primary key (rtmWqWtchDtlDt, rtmWqWtchStaCd)
                );""".trimIndent()

    stmt.executeUpdate(sql)

}

fun makeUrl_RealTimeOceanWaterQuality():String {

    val now = Clock.System.now()

    @OptIn(FormatStringsInDatetimeFormats::class)
    val currentTime = now
        .toLocalDateTime(TimeZone.of("Asia/Seoul"))
        .format(LocalDateTime.Format{byUnicodePattern("yyyy-MM-dd HH:mm:ss")})

    @OptIn(FormatStringsInDatetimeFormats::class)
    val previous24Hour = now
        .minus(1, DateTimeUnit.HOUR)
        .toLocalDateTime(TimeZone.of("Asia/Seoul"))
        .format(LocalDateTime.Format{byUnicodePattern("yyyy-MM-dd HH:mm:ss")})

    LOGGER.info("Current time : ${currentTime}, Previous time : ${previous24Hour}")

    val serviceKeyFilePath = "/Volumes/WorkSpace/Notebook/data/OceanWaterQuality.json"
    val numOfRows = 100
    val maxPage = 1
    val serviceInfo = DataRow.readJson(path=serviceKeyFilePath)

    LOGGER.info(serviceInfo.print().toString())

    val endPoint = serviceInfo["endpoint_xml"]
    val id = serviceInfo["rtmObservationInfo"]
    val key = serviceInfo["key"]

    val url_RtmObInfo = "${endPoint}/${id}" +
            "?wtch_dt_start=${URLEncoder.encode(previous24Hour, StandardCharsets.UTF_8.toString())}" +
            "&wtch_dt_end=${URLEncoder.encode(currentTime, StandardCharsets.UTF_8.toString())}" +
            "&_type=xml" +
            "&numOfRows=${numOfRows}" +
            "&serviceKey=${key}"

    LOGGER.info("RtmObservationInfo Url:[" + url_RtmObInfo + "]")
    return url_RtmObInfo
}

