package org.example.ktor

import io.ktor.util.logging.*
import kotlinx.datetime.format.FormatStringsInDatetimeFormats

val LOGGER = KtorSimpleLogger( "client")

@Suppress("DefaultLocale")
@OptIn(FormatStringsInDatetimeFormats::class)
fun main() {

    try {
        getRealTimeObservation()
        getRealTimeObservatory()
        getRealTimeOceanWaterQuailty()
    } catch (e:Exception) {
        LOGGER.error(e.stackTrace.toString())
    }
}
