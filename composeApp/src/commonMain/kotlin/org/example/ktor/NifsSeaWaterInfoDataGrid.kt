package org.example.ktor

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.example.ktor.data.DATA_DIVISION


@Composable
fun NifsSeaWaterInfoDataGrid(modifier: Modifier = Modifier) {

    val viewModel = remember { NifsSeaWaterInfoOneDayGridViewModel() }

    LaunchedEffect(key1 = viewModel){
        viewModel.onEvent(NifsSeaWaterInfoOneDayGridViewModel.Event.ObservationRefresh(DATA_DIVISION.grid))
        while(true){
            delay(1800 * 1000).let {
                viewModel.onEvent(NifsSeaWaterInfoOneDayGridViewModel.Event.ObservationRefresh(DATA_DIVISION.grid))
            }
        }
    }

    val seaWaterInfoCurrent = viewModel._seaWaterInfoOneDayGridStateFlow.collectAsState()

    if(seaWaterInfoCurrent.value.isNotEmpty()){

            val columnNames = listOf("수집시간", "해역", "관측지점", "지점코드", "수심", "수온", "경도", "위도")
            val data = seaWaterInfoCurrent.value.map { it.toList() }


            ComposeDataGrid(
                modifier = modifier,
                columnNames = columnNames,
                data = data,
            )
    }



}

