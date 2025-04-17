package org.example.ktor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.example.ktor.data.DATA_DIVISION
import org.example.ktor.data.NifsRepository
import org.example.ktor.model.SeawaterInformationByObservationPoint


class NifsSeaWaterInfoCurrentViewModel: ViewModel() {

    private val repository: NifsRepository
        = getPlatform().nifsRepository


    val _seaWaterInfoCurrentStateFlow: MutableStateFlow<List<SeawaterInformationByObservationPoint>>
            = MutableStateFlow(emptyList())


    init {
        viewModelScope.launch {

            repository._seaWaterInfoCurrentStateFlow.collectLatest {
                _seaWaterInfoCurrentStateFlow.value = it
            }

        }
    }

    suspend fun onEvent(event: Event) {
        when (event) {
            is Event.ObservationRefresh -> {
                getSeaWaterInfo(event.division)
            }

            is Event.SortOrder -> {
                when (event.columnName) {
                    "gru_nam" -> sortOrderGruNam(event.sortOrder)
                    else -> {}
                }

            }
        }
    }


    suspend fun getSeaWaterInfo(division: DATA_DIVISION){
        repository.getSeaWaterInfo(division)
    }

    suspend fun sortOrderGruNam( sortOrder:String) {
          val sortedData = when(sortOrder){
            "ASC" -> {
                _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.gru_nam
                }.toList().sortedBy { it.second }.map { it.first }
            }
            "DESC" -> {
                _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.gru_nam
                }.toList().sortedByDescending { it.second }.map { it.first }
            }
            else -> {_seaWaterInfoCurrentStateFlow.value}
        }
        _seaWaterInfoCurrentStateFlow.emit(sortedData)
    }




    sealed class Event {
        data class ObservationRefresh(val division: DATA_DIVISION) : Event()
        data class SortOrder(val columnName: String, val sortOrder:String) : Event()
    }

}