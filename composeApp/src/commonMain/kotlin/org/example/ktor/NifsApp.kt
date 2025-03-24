package org.example.ktor

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun NifsApp() {

    MaterialTheme {

        val viewModel = remember {
            NifsBarsViewModel()
        }
        val seaWaterInfoCurrent = viewModel._seaWaterInfoCurrentStateFlow.collectAsState().value
        val eastSeaWaterInfo = seaWaterInfoCurrent.filter {
            it.gru_nam.equals("동해")
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            items(eastSeaWaterInfo.count()) { index ->
                Text("${eastSeaWaterInfo[index].sta_nam_kor}:${eastSeaWaterInfo[index].obs_lay}:${eastSeaWaterInfo[index].wtr_tmp}:${eastSeaWaterInfo[index].lon} :${eastSeaWaterInfo[index].lat}    "  )
            }
        }

    }

}

