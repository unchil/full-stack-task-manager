package org.example.ktor.data

import org.example.ktor.model.Observation
import org.example.ktor.model.Observatory
import org.example.ktor.model.SeaWaterInfoByOneHourStat
import org.example.ktor.model.SeawaterInformationByObservationPoint

interface NifsRepositoryInterface {

    suspend fun seaWaterInfo(division:String):List<SeawaterInformationByObservationPoint>
    suspend fun seaWaterInfoStatistics():List<SeaWaterInfoByOneHourStat>
    suspend fun observationList(division:String):List<Observation>
    suspend fun observatoryInfo():List<Observatory>
}