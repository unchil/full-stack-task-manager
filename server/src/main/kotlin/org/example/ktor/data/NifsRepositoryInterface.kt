package org.example.ktor.data

import org.example.ktor.model.Observation
import org.example.ktor.model.Observatory
import org.example.ktor.model.RealTimeObservation

interface NifsRepositoryInterface {

    suspend fun observationRealTime():List<RealTimeObservation>
    suspend fun observationList(division:String):List<RealTimeObservation>
    suspend fun observatoryInfo():List<Observatory>
}