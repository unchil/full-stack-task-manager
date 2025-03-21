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

        val scope = rememberCoroutineScope()
        val viewModel = remember {
            NifsViewModel(scope)
        }

        val observationCurrent = viewModel._observationCurrentStateFlow.collectAsState().value

        val eastObservation = observationCurrent.filter {
            it.gru_nam.equals("동해")
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            items(eastObservation.count()) { index ->
                Text("Observatory Info:${eastObservation[index].sta_nam_kor}"  )
            }
        }






    }

}