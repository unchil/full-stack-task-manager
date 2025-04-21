package org.example.ktor

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.ktor.data.DATA_DIVISION

@Composable
fun NifsSeaWaterInfoDataGrid() {

    val viewModel = remember { NifsSeaWaterInfoCurrentViewModel() }

    LaunchedEffect(key1 = viewModel){
        viewModel.onEvent(NifsSeaWaterInfoCurrentViewModel.Event.ObservationRefresh(DATA_DIVISION.current))
        while(true){
            delay(1800 * 1000).let {
                viewModel.onEvent(NifsSeaWaterInfoCurrentViewModel.Event.ObservationRefresh(DATA_DIVISION.current))
            }
        }
    }

    val seaWaterInfoCurrent = viewModel._gridDataStateFlow.collectAsState()

    if(seaWaterInfoCurrent.value.isNotEmpty()){

        val columnNames = listOf("수집시간", "해역", "관측지점", "지점코드", "수심", "수온", "경도", "위도")
        val data = seaWaterInfoCurrent.value.map { it.toList() }

        ComposeDataGrid(
            columnNames = columnNames,
            data = data,
        )

    }
}

