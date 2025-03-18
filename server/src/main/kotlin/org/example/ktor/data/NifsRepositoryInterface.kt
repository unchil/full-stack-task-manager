package org.example.ktor.data

import org.example.ktor.model.Observation
import org.example.ktor.model.Observatory

interface NifsRepositoryInterface {

    suspend fun observationList(division:String):List<Observation>
    suspend fun observatoryInfo():List<Observatory>
}