package org.example.ktor

import kotlinx.coroutines.delay
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class DataCollector() {
    private val collectionInterval = Config.interval.toInt().toDuration(DurationUnit.MINUTES)
    val repository = Repository()
    suspend fun startCollecting() {
        while(true){
            LOGGER.info("Data Collector Job Started.\nType:[${Config.jobType}], Event[${Config.jobEvent}]")
            try {
                repository.getRealTimeObservation()
                repository.getRealTimeObservatory()
                if(Config.jobType.equals("batch") && Config.jobEvent.equals("recovery")){
                    getRealTimeOceanWaterQuality_Rocovery(Config.wtch_dt_start, Config.wtch_dt_end)
                }else{
                    repository.getRealTimeOceanWaterQuality()
                }

            } catch (e: Exception) {
                LOGGER.error(e.stackTrace.toString())
            }

            if(Config.jobType.equals("batch")) break

            LOGGER.info("Data Collector Started. Collecting every ${collectionInterval} minutes...")
            delay(collectionInterval)
        }


    }


}