package org.example.ktor

import kotlinx.datetime.*
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.read
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import org.jetbrains.kotlinx.dataframe.io.toCsvStr
import org.json.XML
import java.io.InputStreamReader
import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.sql.Connection
import java.sql.DriverManager
import kotlin.io.byteInputStream
import kotlin.io.readText
import kotlin.use


fun getRealTimeObservation( ){

    val jdbcUrl = (Config.config_df["SQLITE_DB"] as DataRow<*>)["jdbcURL"].toString()

    val urlString = makeUrl(::getRealTimeObservation.name)
    val response = java.net.URI(urlString).toURL().openStream().use { inputStream ->
        InputStreamReader(inputStream, Charset.forName("EUC-KR")).readText()
    }
    LOGGER.debug("\n"+ response)

    var df = DataFrame.readJson(response.byteInputStream())

    LOGGER.debug("\n"+ df.schema().toString())

    df = df.get("body").get("item").get(0) as AnyFrame

    LOGGER.debug("\n"+ df.schema().toString())
    LOGGER.info("\n"+ df.describe().toString())
    LOGGER.debug("\n"+ df.head(5).toString())

    val tableName = "Observation"

    try {
        DriverManager.getConnection(jdbcUrl).use { conn ->

            val sql = """INSERT INTO ${tableName} ( 
                    sta_cde, sta_nam_kor, obs_dat, obs_tim, obs_datetime,
                    repair_gbn, obs_lay, wtr_tmp, dox, sal 
                ) VALUES (?,?,?,?,?,?,?,?,?,? )""".trimIndent()

            LOGGER.debug("\n"+ sql)

            df.forEach { it ->
                try {
                    conn.prepareStatement(sql)?.use { preparedStatement ->
                        preparedStatement.setString(1, it["sta_cde"].toString())
                        preparedStatement.setString(2, it["sta_nam_kor"].toString())
                        preparedStatement.setString(3, it["obs_dat"].toString())
                        preparedStatement.setString(4, it["obs_tim"].toString())
                        preparedStatement.setString(5, "${it["obs_dat"].toString()} ${it["obs_tim"].toString()}")
                        preparedStatement.setString(6, it["repair_gbn"].toString())
                        preparedStatement.setString(7, it["obs_lay"].toString())
                        preparedStatement.setString(8, it["wtr_tmp"].toString())
                        preparedStatement.setString(9, it["dox"].toString())
                        preparedStatement.setString(10, it["sal"].toString())

                        preparedStatement.executeUpdate()
                    }
                } catch (e: Exception){
                    LOGGER.debug(e.localizedMessage)
                }
            }

        }
    } catch (e: Exception){
        LOGGER.error(e.localizedMessage)
    }

}

fun getRealTimeObservatory(){

    val jdbcUrl = (Config.config_df["SQLITE_DB"] as DataRow<*>)["jdbcURL"].toString()

    val urlString = makeUrl(::getRealTimeObservatory.name)
    val response = java.net.URI(urlString).toURL().openStream().use { inputStream ->
        InputStreamReader(inputStream, Charset.forName("EUC-KR")).readText()
    }

    LOGGER.debug("\n"+ response)

    var df = DataFrame.readJson(response.byteInputStream())

    LOGGER.debug("\n"+ df.schema().toString())

    df = df.get("body").get("item").get(0) as AnyFrame

    LOGGER.debug("\n"+ df.schema().toString())
    LOGGER.info("\n"+ df.describe().toString())
    LOGGER.debug("\n"+ df.head(5).toString())

    val tableName = "Observatory"


    try {
        DriverManager.getConnection(jdbcUrl).use { conn ->

            val sql = """INSERT INTO ${tableName} ( 
                    sta_cde, sta_nam_kor, bld_dat, end_dat, gru_nam,
                    lon, lat, sur_tmp_yn, mid_tmp_yn, bot_tmp_yn,
                     sur_dep, mid_dep, bot_dep, sta_des
                ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,? )""".trimIndent()

            LOGGER.debug("\n"+ sql)

            df.forEach { it ->
                try {
                    conn.prepareStatement(sql)?.use { preparedStatement ->
                        preparedStatement.setString(1, it["sta_cde"].toString())
                        preparedStatement.setString(2, it["sta_nam_kor"].toString())
                        preparedStatement.setString(3, it["bld_dat"].toString())
                        preparedStatement.setString(4, it["end_dat"].toString())
                        preparedStatement.setString(5, it["gru_nam"].toString())
                        preparedStatement.setDouble(6, it["lon"].toString().toDouble())
                        preparedStatement.setDouble(7, it["lat"].toString().toDouble())
                        preparedStatement.setString(8, it["sur_tmp_yn"].toString())
                        preparedStatement.setString(9, it["mid_tmp_yn"].toString())
                        preparedStatement.setString(10, it["bot_tmp_yn"].toString())
                        preparedStatement.setString(11, it["sur_dep"].toString())
                        preparedStatement.setString(12, it["mid_dep"].toString())
                        preparedStatement.setString(13, it["bot_dep"].toString())
                        preparedStatement.setString(14, it["sta_des"].toString())

                        preparedStatement.executeUpdate()
                    }
                } catch (e: Exception){
                    LOGGER.debug(e.localizedMessage)
                }
            }

        }
    } catch (e: Exception){
        LOGGER.error(e.localizedMessage)
    }

}

fun getRealTimeOceanWaterQuailty(){

    val jdbcUrl = (Config.config_df["SQLITE_DB"] as DataRow<*>)["jdbcURL"].toString()

    val url = makeUrl(::getRealTimeOceanWaterQuailty.name)
    val pagePath = "$url&pageNo=1"
    val jsonData = XML.toJSONObject(DataFrame.read(pagePath).toCsvStr())
    var df = DataFrame.readJsonStr(jsonData.toString().trimIndent())

    LOGGER.debug("\n"+ df.schema().toString())

    df = df.get("response").get("body").get("items").get("item")[0] as DataFrame<*>

    df = df
        .convert{ colsAtAnyDepth().colsOf<Double>() }.with { String.format("%.3f", it) }
        .convert { colsAtAnyDepth().colsOf<Float>() }.with{ String.format("%.3f", it)}
        .convert("rtmWqWtchDtlDt").with { (it as String).substring(0, 19) }

    LOGGER.debug("\n"+ df.schema().toString())
    LOGGER.info("\n"+ df.describe().toString())
    LOGGER.debug("\n"+ df.head(5).toString())

    val tableName = "OWQInformation"

    try {
        DriverManager.getConnection(jdbcUrl).use {conn ->

            createAndPopulateTable(conn, tableName)

            val sql = """INSERT INTO ${tableName} ( 
                    rtmWqWtchDtlDt, rtmWqWtchStaCd, rtmWtchWtem, rtmWqCndctv,
                    ph, rtmWqDoxn, rtmWqTu, rtmWqBgalgsQy, rtmWqChpla, rtmWqSlnty 
                ) VALUES (?,?,?,?,?,?,?,?,?,? )""".trimIndent()

            LOGGER.debug("\n"+ sql)

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
                    LOGGER.debug(e.localizedMessage)
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

fun makeUrl(funcName:String):String {


    val urlString = when (funcName) {
        ::getRealTimeObservation.name -> {
            val confData = Config.config_df["NIFS_API"] as DataRow<*>
            "${confData["endPoint"].toString()}/${confData["subPath"].toString()}" +
                    "?id=${ (confData["id"] as DataRow<*>)["list"].toString()}&key=${ confData["apikey"].toString()}"
        }
        ::getRealTimeObservatory.name -> {
            val confData = Config.config_df["NIFS_API"] as DataRow<*>
            "${confData["endPoint"].toString()}/${confData["subPath"].toString()}" +
                    "?id=${ (confData["id"] as DataRow<*>)["code"].toString()}&key=${ confData["apikey"].toString()}"
        }
        ::getRealTimeOceanWaterQuailty.name -> {
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

            LOGGER.debug("Current time : ${currentTime}, Previous time : ${previous24Hour}")

            val numOfRows = 100

            val confData = Config.config_df["MOF_API"] as DataRow<*>

            "${confData["endPoint"]}/${confData["subPath"]}" +
                    "?wtch_dt_start=${URLEncoder.encode(previous24Hour, StandardCharsets.UTF_8.toString())}" +
                    "&wtch_dt_end=${URLEncoder.encode(currentTime, StandardCharsets.UTF_8.toString())}" +
                    "&_type=xml" +
                    "&numOfRows=${numOfRows}" +
                    "&serviceKey=${confData["apikey"]}"


        }
        else -> {""}
    }

    return urlString
}