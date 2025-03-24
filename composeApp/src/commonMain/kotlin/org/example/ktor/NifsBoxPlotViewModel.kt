package org.example.ktor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.example.ktor.data.NifsRepository
import org.example.ktor.model.SeawaterInformationByObservationPoint

class NifsBoxPlotViewModel: ViewModel () {

    private val repository: NifsRepository
            = getPlatform().nifsRepository


    val _seaWaterInfoOneDayStateFlow: MutableStateFlow<List<SeawaterInformationByObservationPoint>>
            = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
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