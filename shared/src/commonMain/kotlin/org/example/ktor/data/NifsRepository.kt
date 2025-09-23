package org.example.ktor.data

import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.coroutines.flow.MutableStateFlow
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
                    nifsApi.getSeaWaterInfo(DATA_DIVISION.oneday.name)?.let { it ->
                        _seaWaterInfoOneDayStateFlow.value = it
                        LOGGER.debug("getSeaWaterInfo() called[${it.count()}]")
                    }
                }
                DATA_DIVISION.grid -> {
                    nifsApi.getSeaWaterInfo(DATA_DIVISION.grid.name)?.let { it ->
                        _seaWaterInfoOneDayGridStateFlow.value = it
                        LOGGER.debug("getSeaWaterInfo() called[${it.count()}]")
                    }
                }
                DATA_DIVISION.current -> {
                    nifsApi.getSeaWaterInfo(DATA_DIVISION.current.name)?.let { it ->
                        _seaWaterInfoCurrentStateFlow.value = it
                        LOGGER.debug("getSeaWaterInfo() called[${it.count()}]")
                    }
                }
                else -> {
                    _seaWaterInfoCurrentStateFlow.value =emptyList()
                }
            }

        }catch (e:Exception){
            LOGGER.error(e.message ?: "Error ")
        }
    }

    suspend fun getSeaWaterInfoStat() {
        try {
            nifsApi.getSeaWaterInfoStat()?.let { it ->
                _seaWaterInfoStatStateFlow.value = it
                LOGGER.debug("getSeaWaterInfoStat() called[${it.count()}]")
            }

        }catch (e:Exception){
            LOGGER.error(e.message ?: "Error ")
        }
    }

    suspend fun getObservatory() {
        try {
            nifsApi.getObservatory()?.let { it ->
                _observatoryStateFlow.value = it
                LOGGER.debug("getObservatory() called[${it.count()}]")
            }

        }catch (e:Exception){
            LOGGER.error(e.message ?: "Error ")
        }
    }

    suspend fun getSeaWaterInfoValues(division: String) : List<SeawaterInformationByObservationPoint> {
        var result: List<SeawaterInformationByObservationPoint> = emptyList()
        try {
            nifsApi.getSeaWaterInfo(division)?.let { it ->
                result = it
            }
        }catch (e:Exception){
            LOGGER.error(e.message ?: "Error ")
        }
        return result
    }

    suspend fun getSeaWaterInfoStatValues() : List<SeaWaterInfoByOneHourStat> {
        var result: List<SeaWaterInfoByOneHourStat> = emptyList()
        try {
            nifsApi.getSeaWaterInfoStat()?.let {
                result = it
            }
        }catch (e:Exception){
            LOGGER.error(e.message ?: "Error ")
        }
        return result
    }


}