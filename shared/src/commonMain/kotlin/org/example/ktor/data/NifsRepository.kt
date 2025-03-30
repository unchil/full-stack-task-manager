package org.example.ktor.data

import io.ktor.util.logging.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import org.example.ktor.model.GeoJson
import org.example.ktor.model.Observatory
import org.example.ktor.model.SeaWaterInfoByOneHourStat
import org.example.ktor.model.SeawaterInformationByObservationPoint
import org.example.ktor.network.NifsApi

import io.ktor.utils.io.core.*


class NifsRepository {

    internal val LOGGER = KtorSimpleLogger( "NifsRepository" )

    private val nifsApi = NifsApi()

    val _seaWaterInfoOneDayStateFlow: MutableStateFlow<List<SeawaterInformationByObservationPoint>>
        = MutableStateFlow(emptyList())

    val _seaWaterInfoCurrentStateFlow: MutableStateFlow<List<SeawaterInformationByObservationPoint>>
            = MutableStateFlow(emptyList())

    val _seaWaterInfoStatStateFlow: MutableStateFlow<List<SeaWaterInfoByOneHourStat>>
            = MutableStateFlow(emptyList())

    val _observatoryStateFlow: MutableStateFlow<List<Observatory>>
        = MutableStateFlow(emptyList())

    suspend fun getSeaWaterInfo(division: String) {
        try {
            when(division) {
                "oneday" -> {
                    _seaWaterInfoOneDayStateFlow.value = nifsApi.getSeaWaterInfo(division)
                    LOGGER.debug("getSeaWaterInfo() called[${_seaWaterInfoOneDayStateFlow.value.count()}]")
                }
                "current" -> {
                    _seaWaterInfoCurrentStateFlow.value = nifsApi.getSeaWaterInfo(division)
                    LOGGER.debug("getSeaWaterInfo() called[${_seaWaterInfoCurrentStateFlow.value.count()}]")
                }
                else -> {
                    _seaWaterInfoCurrentStateFlow.value =emptyList()
                }
            }

        }catch (e:Exception){
            e.message?.let { LOGGER.error(it) }
        }
    }

    suspend fun getSeaWaterInfoStat() {
        try {
            _seaWaterInfoStatStateFlow.value = nifsApi.getSeaWaterInfoStat()
            LOGGER.debug("getSeaWaterInfoStat() called[${_seaWaterInfoStatStateFlow.value.count()}]")
        }catch (e:Exception){
            e.message?.let { LOGGER.error(it) }
        }
    }

    suspend fun getObservatory() {
        try {
            _observatoryStateFlow.value = nifsApi.getObservatory()
            LOGGER.debug("getObservatory() called[${_observatoryStateFlow.value.count()}]")
        }catch (e:Exception){
            e.message?.let { LOGGER.error(it) }
        }
    }



}