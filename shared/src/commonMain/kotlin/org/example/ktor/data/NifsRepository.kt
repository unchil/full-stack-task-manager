package org.example.ktor.data

import kotlinx.coroutines.flow.MutableStateFlow
import org.example.ktor.model.Observation
import org.example.ktor.model.Observatory
import org.example.ktor.model.RealTimeObservation
import org.example.ktor.network.NifsApi

class NifsRepository {

    private val nifsApi = NifsApi()

    val _observationOneDayStateFlow: MutableStateFlow<List<RealTimeObservation>>
        = MutableStateFlow(emptyList())

    val _observationCurrentStateFlow: MutableStateFlow<List<RealTimeObservation>>
            = MutableStateFlow(emptyList())

    val _observatoryStateFlow: MutableStateFlow<List<Observatory>>
        = MutableStateFlow(emptyList())

    suspend fun getObservations(division: String) {
        try {
            when(division) {
                "oneday" -> {
                    _observationOneDayStateFlow.value = nifsApi.getObservation(division)
                }
                "current" -> {
                    _observationCurrentStateFlow.value = nifsApi.getObservation(division)
                }
                else -> {
                    _observationCurrentStateFlow.value =emptyList()
                }
            }

        }catch (_:Exception){

        }
    }

    suspend fun getObservatory() {
        try {
            _observatoryStateFlow.value = nifsApi.getObservatory()
        }catch (_:Exception){

        }
    }

}