package org.example.ktor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.example.ktor.data.DATA_DIVISION


@Composable
fun NifsSeaWaterInfoDataGrid(modifier: Modifier = Modifier) {

    val viewModel = remember { NifsSeaWaterInfoOneDayGridViewModel() }
    val coroutineScope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(false) }
    val data = remember { mutableStateOf(emptyList<List<Any?>>()) }

    LaunchedEffect(key1 = viewModel){
        viewModel.onEvent(NifsSeaWaterInfoOneDayGridViewModel.Event.ObservationRefresh(DATA_DIVISION.grid))
    }

    val reloadData :()->Unit = {
        coroutineScope.launch{
            viewModel.onEvent(NifsSeaWaterInfoOneDayGridViewModel.Event.ObservationRefresh(DATA_DIVISION.grid))
        }
    }

    val seaWaterInfoCurrent = viewModel._seaWaterInfoOneDayGridStateFlow.collectAsState()

    LaunchedEffect(seaWaterInfoCurrent.value){
        isVisible = seaWaterInfoCurrent.value.isNotEmpty()
        if(isVisible){
            data.value = seaWaterInfoCurrent.value.map { it.toList() }
        }
    }
    val columnNames = listOf("수집시간", "해역", "관측지점", "지점코드", "수심", "수온", "경도", "위도")

    if(isVisible){
        ComposeDataGrid(
            modifier = modifier,
            columnNames = columnNames,
            data = data.value,
            reloadData = reloadData
        )
    }
}

