package org.example.ktor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.example.ktor.data.NifsRepository
import org.example.ktor.model.SeawaterInformationByObservationPoint

class NifsViewModel ( private val scope: CoroutineScope) {

    private val repository: NifsRepository
        = getPlatform().nifsRepository

    val _seaWaterInfoOneDayStateFlow: MutableStateFlow<List<SeawaterInformationByObservationPoint>>
            = MutableStateFlow(emptyList())

    val _seaWaterInfoCurrentStateFlow: MutableStateFlow<List<SeawaterInformationByObservationPoint>>
            = MutableStateFlow(emptyList())



    init {
        scope.launch {
            repository.getObservatory()
            repository.getSeaWaterInfo("current")


            repository._seaWaterInfoCurrentStateFlow.collectLatest {
                _seaWaterInfoCurrentStateFlow.value = it
            }
            repository._seaWaterInfoOneDayStateFlow.collectLatest {
                _seaWaterInfoOneDayStateFlow.value = it
            }
        }
    }

    suspend fun onEvent(event: Event) {
        when (event) {
            is Event.ObservationRefresh -> {
                getSeaWaterInfo(event.division)
            }
            Event.ObservatoryRefresh -> {
                getObservatory()
            }
        }

    }

    suspend fun getSeaWaterInfo(division: String){
        repository.getSeaWaterInfo(division)
    }

    suspend fun getObservatory(){
        repository.getObservatory()
    }

    sealed class Event {
        data class ObservationRefresh(val division: String) : Event()
        data object ObservatoryRefresh : Event()
    }

}