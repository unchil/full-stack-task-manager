package org.example.ktor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.example.ktor.data.DATA_DIVISION
import org.example.ktor.data.NifsRepository
import org.example.ktor.model.SeawaterInformationByObservationPoint

class NifsSeaWaterInfoOneDayGridViewModel: ViewModel () {

    private val repository: NifsRepository
            = getPlatform().nifsRepository


    val _seaWaterInfoOneDayGridStateFlow: MutableStateFlow<List<SeawaterInformationByObservationPoint>>
            = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
            repository._seaWaterInfoOneDayGridStateFlow.collectLatest {
                _seaWaterInfoOneDayGridStateFlow.value = it
            }

        }
    }


    suspend fun onEvent(event: Event) {
        when (event) {
            is Event.ObservationRefresh -> {
                getSeaWaterInfo(event.division)
            }
        }

    }

    suspend fun getSeaWaterInfo(division: DATA_DIVISION){
        repository.getSeaWaterInfo(division)
    }


    sealed class Event {
        data class ObservationRefresh(val division: DATA_DIVISION) : Event()
    }
}