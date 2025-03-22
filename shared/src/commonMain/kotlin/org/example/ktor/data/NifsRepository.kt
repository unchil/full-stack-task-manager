package org.example.ktor.data

import kotlinx.coroutines.flow.MutableStateFlow
import org.example.ktor.model.Observatory
import org.example.ktor.model.SeawaterInformationByObservationPoint
import org.example.ktor.network.NifsApi

class NifsRepository {

    private val nifsApi = NifsApi()

    val _seaWaterInfoOneDayStateFlow: MutableStateFlow<List<SeawaterInformationByObservationPoint>>
        = MutableStateFlow(emptyList())

    val _seaWaterInfoCurrentStateFlow: MutableStateFlow<List<SeawaterInformationByObservationPoint>>
            = MutableStateFlow(emptyList())

    val _observatoryStateFlow: MutableStateFlow<List<Observatory>>
        = MutableStateFlow(emptyList())

    suspend fun getSeaWaterInfo(division: String) {
        try {
            when(division) {
                "oneday" -> {
                    _seaWaterInfoOneDayStateFlow.value = nifsApi.getSeaWaterInfo(division)
                }
                "current" -> {
                    _seaWaterInfoCurrentStateFlow.value = nifsApi.getSeaWaterInfo(division)
                }
                else -> {
                    _seaWaterInfoCurrentStateFlow.value =emptyList()
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