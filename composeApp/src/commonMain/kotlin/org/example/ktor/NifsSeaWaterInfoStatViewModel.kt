package org.example.ktor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.example.ktor.data.NifsRepository
import org.example.ktor.model.SeaWaterInfoByOneHourStat

class NifsSeaWaterInfoStatViewModel: ViewModel() {

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

            Event.ObservationStatRefresh -> {
                getObservationStat()
            }
        }

    }

    suspend fun getObservationStat(){
        repository.getSeaWaterInfoStat()
    }



    sealed class Event {
        data object ObservationStatRefresh: Event()
    }



}