package org.example.ktor.data

import org.example.ktor.model.Observation
import org.example.ktor.model.Observatory
import org.example.ktor.model.SeaWaterInfoByOneHourStat
import org.example.ktor.model.SeawaterInformationByObservationPoint

interface NifsRepositoryInterface {

    suspend fun fetchSeaWaterInfoFromDb(division:String, ):List<SeawaterInformationByObservationPoint>
    suspend fun fetchSeaWaterInfoStatisticsFromDb():List<SeaWaterInfoByOneHourStat>
    suspend fun observatoryInfo():List<Observatory>
}