package org.example.ktor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.example.ktor.data.NifsRepository
import org.example.ktor.model.Observation
import org.example.ktor.model.Observatory
import org.example.ktor.model.RealTimeObservation

class NifsViewModel ( private val scope: CoroutineScope) {

    private val repository: NifsRepository
        = getPlatform().nifsRepository

    val _observationOneDayStateFlow: MutableStateFlow<List<RealTimeObservation>>
            = MutableStateFlow(emptyList())

    val _observationCurrentStateFlow: MutableStateFlow<List<RealTimeObservation>>
            = MutableStateFlow(emptyList())



    init {
        scope.launch {
            repository.getObservatory()
            repository.getObservations("current")


            repository._observationCurrentStateFlow.collectLatest {
                _observationCurrentStateFlow.value = it
            }
            repository._observationOneDayStateFlow.collectLatest {
                _observationOneDayStateFlow.value = it
            }
        }
    }

    suspend fun onEvent(event: Event) {
        when (event) {
            is Event.ObservationRefresh -> {
                getObservations(event.division)
            }
            Event.ObservatoryRefresh -> {
                getObservatory()
            }
        }

    }

    suspend fun getObservations(division: String){
        repository.getObservations(division)
    }

    suspend fun getObservatory(){
        repository.getObservatory()
    }

    sealed class Event {
        data class ObservationRefresh(val division: String) : Event()
        data object ObservatoryRefresh : Event()
    }

}