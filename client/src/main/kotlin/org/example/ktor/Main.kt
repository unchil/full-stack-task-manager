
package org.example.ktor

import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.getSchemaForSqlTable
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import java.sql.DriverManager

val LOGGER = KtorSimpleLogger( "client")


@Suppress("DefaultLocale")
@OptIn(FormatStringsInDatetimeFormats::class)
fun main() = runBlocking {
    val collector = DataCollector()
    collector.startCollecting()
    collector.repository.getRealTimeOceanWaterQuality()
    LOGGER.info("Data Collector Stopped.")
}



fun readTable(){
    val jdbcUrl = (Config.config_df["SQLITE_DB"] as DataRow<*>)["jdbcURL"].toString()
    val dbConfig = DbConnectionConfig(jdbcUrl)
    val observation_df = DataFrame.readSqlTable(dbConfig, "Observation").cast<Observation2>()


    observation_df.filter {it ->
        it["obs_dat"]?.equals("2025-06-17") == true &&  it["obs_lay"]?.equals("1") == true
    }.sortByDesc(  "wtr_tmp").print()


}

fun readTable3(){
    val jdbcUrl = (Config.config_df["SQLITE_DB"] as DataRow<*>)["jdbcURL"].toString()

    DriverManager.getConnection(jdbcUrl).use { connection ->
        val df = DataFrame.readSqlQuery(connection, "Select * from Observatory", 100).cast<ObservatoryTable>()

        df.print()
    }



}

fun readTable2(){
    val jdbcUrl = (Config.config_df["SQLITE_DB"] as DataRow<*>)["jdbcURL"].toString()
    val TABLE_OBSERVATION = "Observation"
    DriverManager.getConnection(jdbcUrl).use { connection ->
        val observation_df = DataFrame.readSqlTable(connection, TABLE_OBSERVATION, 100).cast<ObservationTable>()
        observation_df.print()

        val observationSchema = DataFrame.getSchemaForSqlTable(connection, TABLE_OBSERVATION)
        observationSchema.print()
    }
}

