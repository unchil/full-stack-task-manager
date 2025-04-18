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
                    "sta_nam_kor" -> sortOrderStaNam(event.sortOrder)
                    "obs_datetime" -> sortOrderDateTime(event.sortOrder)
                    "obs_lay" -> sortOrderObsLay(event.sortOrder)
                    "wtr_tmp" -> sortOrderWtrTmp(event.sortOrder)
                    "lon" -> sortOrderLon(event.sortOrder)
                    "lat" -> sortOrderLat(event.sortOrder)
                    "sta_cde" -> sortOrderStaCde(event.sortOrder)
                    else -> {}
                }

            }

            is Event.SearchData -> searchData(event.columnName, event.searchText)
        }
    }


    suspend fun getSeaWaterInfo(division: DATA_DIVISION){
        repository.getSeaWaterInfo(division)
    }

    suspend fun searchData(columnName: String, searchText: String){

        val result = repository.getSeaWaterInfoValues("current").filter {
            it.gru_nam == searchText
        }
        _seaWaterInfoCurrentStateFlow.value = result
    }


    suspend fun sortOrderStaCde( sortOrder:String) {

        when(sortOrder){
            "ASC" -> {
                val result = _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.sta_cde
                }.toList().sortedBy { it.second }.map { it.first }
                _seaWaterInfoCurrentStateFlow.value = result
            }
            "DESC" -> {
                val result = _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.sta_cde
                }.toList().sortedByDescending { it.second }.map { it.first }

                _seaWaterInfoCurrentStateFlow.value = result
            }
            else -> {
                repository._seaWaterInfoCurrentStateFlow.collectLatest {
                    _seaWaterInfoCurrentStateFlow.value = it
                }
            }
        }

    }
    suspend fun sortOrderDateTime( sortOrder:String) {

        when(sortOrder){
            "ASC" -> {
                val result = _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.obs_datetime
                }.toList().sortedBy { it.second }.map { it.first }

                _seaWaterInfoCurrentStateFlow.value = result
            }
            "DESC" -> {
                val result = _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.obs_datetime
                }.toList().sortedByDescending { it.second }.map { it.first }

                _seaWaterInfoCurrentStateFlow.value = result
            }
            else -> {
                repository._seaWaterInfoCurrentStateFlow.collectLatest {
                    _seaWaterInfoCurrentStateFlow.value = it
                }
            }
        }

    }
    suspend fun sortOrderObsLay( sortOrder:String) {

        when(sortOrder){
            "ASC" -> {
                val result = _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.obs_lay
                }.toList().sortedBy { it.second }.map { it.first }

                _seaWaterInfoCurrentStateFlow.value = result
            }
            "DESC" -> {
                val result = _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.obs_lay
                }.toList().sortedByDescending { it.second }.map { it.first }

                _seaWaterInfoCurrentStateFlow.value = result
            }
            else -> {
                repository._seaWaterInfoCurrentStateFlow.collectLatest {
                    _seaWaterInfoCurrentStateFlow.value = it
                }
            }
        }

    }
    suspend fun sortOrderWtrTmp( sortOrder:String) {

        when(sortOrder){
            "ASC" -> {
                val result = _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.wtr_tmp
                }.toList().sortedBy { it.second.toDouble() }.map { it.first }

                _seaWaterInfoCurrentStateFlow.value = result
            }
            "DESC" -> {
                val result = _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.wtr_tmp
                }.toList().sortedByDescending { it.second.toDouble() }.map { it.first }

                _seaWaterInfoCurrentStateFlow.value = result
            }
            else -> {
                repository._seaWaterInfoCurrentStateFlow.collectLatest {
                    _seaWaterInfoCurrentStateFlow.value = it
                }
            }
        }

    }

    suspend fun sortOrderLon(sortOrder:String) {

        when(sortOrder){
            "ASC" -> {
                val result = _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.lon
                }.toList().sortedBy { it.second }.map { it.first }

                _seaWaterInfoCurrentStateFlow.value = result

            }
            "DESC" -> {
                val result = _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.lon
                }.toList().sortedByDescending { it.second }.map { it.first }

                _seaWaterInfoCurrentStateFlow.value = result
            }
            else -> {
                repository._seaWaterInfoCurrentStateFlow.collectLatest {
                    _seaWaterInfoCurrentStateFlow.value = it
                }
            }
        }

    }

    suspend fun sortOrderLat(sortOrder:String) {

        when(sortOrder){
            "ASC" -> {
                val result = _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.lat
                }.toList().sortedBy { it.second }.map { it.first }

                _seaWaterInfoCurrentStateFlow.value = result
            }
            "DESC" -> {
                val result = _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.lat
                }.toList().sortedByDescending { it.second }.map { it.first }

                _seaWaterInfoCurrentStateFlow.value = result
            }
            else -> {
                repository._seaWaterInfoCurrentStateFlow.collectLatest {
                    _seaWaterInfoCurrentStateFlow.value = it
                }
            }
        }

    }





    suspend fun sortOrderGruNam(sortOrder:String) {

          when(sortOrder){
            "ASC" -> {
                val result = _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.gru_nam
                }.toList().sortedBy { it.second }.map { it.first }

                _seaWaterInfoCurrentStateFlow.value = result
            }
            "DESC" -> {
                val result = _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.gru_nam
                }.toList().sortedByDescending { it.second }.map { it.first }

                _seaWaterInfoCurrentStateFlow.value = result
            }
            else -> {
                repository._seaWaterInfoCurrentStateFlow.collectLatest {
                    _seaWaterInfoCurrentStateFlow.value = it
                }
            }
        }

    }


    suspend fun sortOrderStaNam(sortOrder:String) {

        when(sortOrder){
            "ASC" -> {
                val result = _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.sta_nam_kor
                }.toList().sortedBy { it.second }.map { it.first }

                _seaWaterInfoCurrentStateFlow.value = result
            }
            "DESC" -> {
                val result = _seaWaterInfoCurrentStateFlow.value.map {
                    it to it.sta_nam_kor
                }.toList().sortedByDescending { it.second }.map { it.first }

                _seaWaterInfoCurrentStateFlow.value = result
            }
            else -> {
                repository._seaWaterInfoCurrentStateFlow.collectLatest {
                    _seaWaterInfoCurrentStateFlow.value = it
                }
            }
        }

    }





    sealed class Event {
        data class ObservationRefresh(val division: DATA_DIVISION) : Event()
        data class SortOrder(val columnName: String, val sortOrder:String) : Event()
        data class SearchData(val columnName: String, val searchText: String) : Event()
    }

}