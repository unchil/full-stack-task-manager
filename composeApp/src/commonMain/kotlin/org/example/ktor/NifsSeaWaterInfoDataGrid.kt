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
import com.unchil.un7datagrid.Un7KCMPDataGrid



fun toInstantMap(first:List<String>, second:List<List<Any?>>):Map<String, List<Any?>>{
    val result = mutableMapOf<String, List<Any?>>()
    if(first.size == second.first().size) {
        first.forEachIndexed { index, string ->
            result.putAll(mapOf(string to second.map { it -> it[index] }.toList()) )
        }
    }
    return result
}



@Composable
fun NifsSeaWaterInfoDataGrid(modifier: Modifier = Modifier) {


    val viewModel = remember { MofSeaWaterInfoOneDayViewModel() }
    val coroutineScope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = viewModel){
        viewModel.onEvent(MofSeaWaterInfoOneDayViewModel.Event.ObservationRefresh(DATA_DIVISION.mof_oneday))
    }

    val reloadData :()->Unit = {
        coroutineScope.launch{
            viewModel.onEvent(MofSeaWaterInfoOneDayViewModel.Event.ObservationRefresh(DATA_DIVISION.mof_oneday))
        }
    }

    val seaWaterInfo = viewModel._seaWaterInfoOneDayStateFlow.collectAsState()

    val columnNames = remember { mutableStateOf(emptyList<String>() ) }
    val data = remember { mutableStateOf(emptyList<List<Any?>>()) }

    LaunchedEffect(seaWaterInfo.value){

        isVisible = seaWaterInfo.value.isNotEmpty()
        if(isVisible){
            columnNames.value = seaWaterInfo.value.first().makeGridColumns()
            data.value = seaWaterInfo.value.map {
                it.toGridData()
            }
        }
    }

    if(isVisible){
        Un7KCMPDataGrid(modifier, toInstantMap(columnNames.value , data.value))

    }
}

