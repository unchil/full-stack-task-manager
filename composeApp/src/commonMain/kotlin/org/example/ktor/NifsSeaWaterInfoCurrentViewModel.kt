package org.example.ktor

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import org.example.ktor.data.DATA_DIVISION
import org.example.ktor.data.NifsRepository
import org.example.ktor.model.SeawaterInformationByObservationPoint


class NifsSeaWaterInfoCurrentViewModel: ViewModel() {

    private val repository: NifsRepository
        = getPlatform().nifsRepository

    val _seaWaterInfoCurrentStateFlow: MutableStateFlow<List<SeawaterInformationByObservationPoint>>
            = MutableStateFlow(emptyList())

    val _gridDataStateFlow: MutableStateFlow<List<SeawaterInformationByObservationPoint>>
            = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
            repository._seaWaterInfoCurrentStateFlow.collectLatest {
                _seaWaterInfoCurrentStateFlow.value = it
                _gridDataStateFlow.value = it
            }
        }
    }

    suspend fun onEvent(event: Event) {
        when (event) {
            is Event.ObservationRefresh -> {
                getSeaWaterInfo(event.division)
            }

            is Event.SortOrder -> {
                when(event.columnName){
                    "wtr_tmp" -> sortOrderDouble(event.columnName, event.sortOrder)
                    else -> sortOrderString(event.columnName, event.sortOrder)
                }
            }

            is Event.SearchData -> searchData(event.columnName, event.searchText)
        }
    }

    suspend fun getSeaWaterInfo(division: DATA_DIVISION){
        repository.getSeaWaterInfo(division)
        repository._seaWaterInfoCurrentStateFlow.collectLatest {
            _seaWaterInfoCurrentStateFlow.value = it
        }
    }

    suspend fun searchData(columnName: String, searchText: String){
        val result = when(columnName){
            "gru_nam" -> { repository.getSeaWaterInfoValues("current").filter {
                it.gru_nam == searchText } }
            "sta_nam_kor" -> {  repository.getSeaWaterInfoValues("current").filter {
                it.sta_nam_kor == searchText } }
            "obs_lay" -> { repository.getSeaWaterInfoValues("current").filter {
                it.obs_lay == searchText } }
            "sta_cde" -> { repository.getSeaWaterInfoValues("current").filter {
                it.sta_cde == searchText } }
            else -> {repository.getSeaWaterInfoValues("current")}
        }
        _seaWaterInfoCurrentStateFlow.value = result
    }

    suspend fun sortOrderString(columnName: String, sortOrder:String){
        val data = when(columnName){
            "gru_nam" -> {
                _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.gru_nam
                }.toList()
            }
            "sta_nam_kor" -> {
                _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.sta_nam_kor
                }.toList()
            }
            "obs_lay" -> {
                _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.obs_lay
                }.toList()
            }
            "sta_cde" -> {
                _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.sta_cde
                }.toList()
            }
            else -> {
                _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.obs_datetime
                }.toList()
            }
        }

       when(sortOrder){
            "ASC" -> {
                _seaWaterInfoCurrentStateFlow.value = data.sortedBy { it.second }.map { it.first }
            }
            "DESC" -> {
                _seaWaterInfoCurrentStateFlow.value = data.sortedByDescending { it.second }.map { it.first }
            }
            else -> {
                getSeaWaterInfo(DATA_DIVISION.current)
            }
        }
    }

    suspend fun sortOrderDouble(columnName: String, sortOrder:String){
        val data = when(columnName){
            "wtr_tmp" -> {
                _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.wtr_tmp.toDouble()
                }.toList()
            }
            else -> {
                _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.wtr_tmp.toDouble()
                }.toList()
            }

        }
        when(sortOrder){
            "ASC" -> {
                _seaWaterInfoCurrentStateFlow.value = data.sortedBy { it.second }.map { it.first }
            }
            "DESC" -> {
                _seaWaterInfoCurrentStateFlow.value = data.sortedByDescending { it.second }.map { it.first }
            }
            else -> {
                getSeaWaterInfo(DATA_DIVISION.current)
            }
        }
    }


    sealed class Event {
        data class ObservationRefresh(val division: DATA_DIVISION) : Event()
        data class SortOrder(val columnName: String, val sortOrder:String) : Event()
        data class SearchData(val columnName: String, val searchText: String) : Event()
    }

}