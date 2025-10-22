package org.example.ktor

import kotlinx.coroutines.delay
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.io.readJson
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class DataCollector() {
    private val collectionInterval = Config.interval.toInt().toDuration(DurationUnit.MINUTES)
    private val repository = Repository()
    suspend fun startCollecting() {
        while(true){
            LOGGER.info("Data Collector Started. Job Type: ${Config.jobType}")
            try {
                repository.getRealTimeObservation()
                repository.getRealTimeObservatory()
            } catch (e: Exception) {
                LOGGER.error(e.stackTrace.toString())
            }

            if(Config.jobType.equals("batch")) break

            LOGGER.info("Data Collector Started. Collecting every ${collectionInterval} minutes...")
            delay(collectionInterval)
        }

        LOGGER.info("Data Collector Stopped.")
    }


}