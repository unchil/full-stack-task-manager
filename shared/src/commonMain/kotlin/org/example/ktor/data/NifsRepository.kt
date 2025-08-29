package org.example.ktor.data

import io.ktor.util.logging.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import org.example.ktor.model.GeoJson
import org.example.ktor.model.Observatory
import org.example.ktor.model.SeaWaterInfoByOneHourStat
import org.example.ktor.model.SeawaterInformationByObservationPoint
import org.example.ktor.network.NifsApi


enum class DATA_DIVISION {
    oneday, grid, current, statistics
}

class NifsRepository {

    internal val LOGGER = KtorSimpleLogger( "NifsRepository" )

    val nifsApi = NifsApi()

    val _seaWaterInfoOneDayStateFlow: MutableStateFlow<List<SeawaterInformationByObservationPoint>>
        = MutableStateFlow(emptyList())

    val _seaWaterInfoOneDayGridStateFlow: MutableStateFlow<List<SeawaterInformationByObservationPoint>>
            = MutableStateFlow(emptyList())

    val _seaWaterInfoCurrentStateFlow: MutableStateFlow<List<SeawaterInformationByObservationPoint>>
            = MutableStateFlow(emptyList())

    val _seaWaterInfoStatStateFlow: MutableStateFlow<List<SeaWaterInfoByOneHourStat>>
            = MutableStateFlow(emptyList())

    val _observatoryStateFlow: MutableStateFlow<List<Observatory>>
        = MutableStateFlow(emptyList())

    suspend fun getSeaWaterInfo(division: DATA_DIVISION) {
        try {
            when(division) {
                DATA_DIVISION.oneday -> {
                    _seaWaterInfoOneDayStateFlow.value = nifsApi.getSeaWaterInfo(DATA_DIVISION.oneday.name)
                    LOGGER.debug("getSeaWaterInfo() called[${_seaWaterInfoOneDayStateFlow.value.count()}]")
                }
                DATA_DIVISION.grid -> {
                    _seaWaterInfoOneDayGridStateFlow.value = nifsApi.getSeaWaterInfo(DATA_DIVISION.grid.name)
                    LOGGER.debug("getSeaWaterInfo() called[${_seaWaterInfoOneDayGridStateFlow.value.count()}]")
                }
                DATA_DIVISION.current -> {
                    _seaWaterInfoCurrentStateFlow.value = nifsApi.getSeaWaterInfo(DATA_DIVISION.current.name)
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

    suspend fun getSeaWaterInfoValues(division: String) : List<SeawaterInformationByObservationPoint> {
        var result: List<SeawaterInformationByObservationPoint> = emptyList()
        try {
            result =  nifsApi.getSeaWaterInfo(division)
        }catch (e:Exception){
            e.message?.let { LOGGER.error(it) }
        }
        return result
    }

    suspend fun getSeaWaterInfoStatValues() : List<SeaWaterInfoByOneHourStat> {
        var result: List<SeaWaterInfoByOneHourStat> = emptyList()
        try {
            result =  nifsApi.getSeaWaterInfoStat()
        }catch (e:Exception){
            e.message?.let { LOGGER.error(it) }
        }
        return result
    }


}