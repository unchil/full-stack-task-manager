
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
    LOGGER.info("Data Collector Stopped.")
}
