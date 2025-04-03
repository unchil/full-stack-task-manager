package org.example.ktor

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun NifsApp() {

    MaterialTheme {

        val viewModel = remember {
            NifsSeaWaterInfoCurrentViewModel()
        }

        LaunchedEffect(key1 = viewModel){
            viewModel.onEvent(NifsSeaWaterInfoCurrentViewModel.Event.ObservationRefresh("current"))
        }

        val seaWaterInfoCurrent = viewModel._seaWaterInfoCurrentStateFlow.collectAsState().value.filter { it.gru_nam.equals("동해") }


        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            items(seaWaterInfoCurrent.count()) { index ->
                Text("${seaWaterInfoCurrent[index].sta_nam_kor}:${seaWaterInfoCurrent[index].obs_lay}:${seaWaterInfoCurrent[index].wtr_tmp}:${seaWaterInfoCurrent[index].lon} :${seaWaterInfoCurrent[index].lat}    "  )
            }
        }

    }

}

