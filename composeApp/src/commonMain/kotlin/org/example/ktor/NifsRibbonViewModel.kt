package org.example.ktor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.example.ktor.data.NifsRepository
import org.example.ktor.model.SeaWaterInfoByOneHourStat

class NifsRibbonViewModel: ViewModel() {

    private val repository: NifsRepository
            = getPlatform().nifsRepository

    val _seaWaterInfoStatStateFlow: MutableStateFlow<List<SeaWaterInfoByOneHourStat>>
        = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
            repository._seaWaterInfoStatStateFlow.collectLatest {
                _seaWaterInfoStatStateFlow.value = it
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

            Event.ObservationStatRefresh -> {
                getObservationStat()
            }
        }

    }

    suspend fun getSeaWaterInfo(division: String){
        repository.getSeaWaterInfo(division)
    }

    suspend fun getObservationStat(){
        repository.getSeaWaterInfoStat()
    }

    suspend fun getObservatory(){
        repository.getObservatory()
    }

    sealed class Event {
        data class ObservationRefresh(val division: String) : Event()
        data object ObservatoryRefresh : Event()
        data object ObservationStatRefresh: Event()
    }



}