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

    val viewModel = remember { NifsSeaWaterInfoOneDayViewModel() }

    LaunchedEffect(key1 = viewModel){
        viewModel.onEvent(NifsSeaWaterInfoOneDayViewModel.Event.ObservationRefresh(DATA_DIVISION.oneday))
        while(true){
            delay(1800 * 1000).let {
                viewModel.onEvent(NifsSeaWaterInfoOneDayViewModel.Event.ObservationRefresh(DATA_DIVISION.oneday))
            }
        }
    }

    val seaWaterInfoCurrent = viewModel._seaWaterInfoOneDayStateFlow.collectAsState()

    if(seaWaterInfoCurrent.value.isNotEmpty()){

            val columnNames = listOf("수집시간", "해역", "관측지점", "지점코드", "수심", "수온", "경도", "위도", "용존산소")
            val data = seaWaterInfoCurrent.value.map { it.toList() }


            ComposeDataGrid(
                modifier = modifier,
                columnNames = columnNames,
                data = data,
            )
    }



}

