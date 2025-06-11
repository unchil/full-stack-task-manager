package org.example.ktor

import io.ktor.util.logging.*
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import org.jetbrains.kotlinx.dataframe.DataRow

val LOGGER = KtorSimpleLogger( "client")


@Suppress("DefaultLocale")
@OptIn(FormatStringsInDatetimeFormats::class)
suspend fun main() {

    val repository = Repository()
    repository.getRealTimeObservation()
    repository.getRealTimeObservatory()
    NifsApi.client.close()

    val jdbcUrl = (Repository.Companion.config_df["SQLITE_DB"] as DataRow<*>)["jdbcURL"].toString()

    try {
        getRealTimeOceanWaterQuailty(jdbcUrl)
    } catch (e:Exception) {
        LOGGER.error(e.stackTrace.toString())
    }
}
