package org.example.ktor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.then
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.ktor.data.DATA_DIVISION
import org.example.ktor.model.SeawaterInformationByObservationPoint


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NifsObservationGrid(modifier:Modifier = Modifier) {

    val viewModel = remember { NifsSeaWaterInfoCurrentViewModel() }

    LaunchedEffect(key1 = viewModel){
        viewModel.onEvent(NifsSeaWaterInfoCurrentViewModel.Event.ObservationRefresh(DATA_DIVISION.current))
        while(true){
            delay(1800 * 1000).let {
                viewModel.onEvent(NifsSeaWaterInfoCurrentViewModel.Event.ObservationRefresh(DATA_DIVISION.current))
            }
        }
    }

    val seaWaterInfoCurrent = viewModel._seaWaterInfoCurrentStateFlow.collectAsState()

    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)

    MaterialTheme {

        Box(
            modifier = Modifier.size(width = 1300.dp, height = 530.dp).padding(bottom = 20.dp)
                .border(
                    border = BorderStroke(width = 1.dp, color = Color.LightGray.copy(alpha = 0.5f)  ),
                    shape = RoundedCornerShape(2.dp)
                )
        ){
            Column(modifier = then(modifier)) {

                HeaderGridRow(modifier = Modifier.fillMaxWidth())


                LazyColumn (
                    modifier =  Modifier.fillMaxWidth().height(400.dp),
                    state = lazyListState,
                    contentPadding = PaddingValues(0.dp),
                    userScrollEnabled = true
                ){

                    items(seaWaterInfoCurrent.value.size){
                        DataGridRow(modifier = then(modifier), data = seaWaterInfoCurrent.value[it])
                    }
                }

                FooterGridRow(modifier = Modifier.fillMaxWidth(), seaWaterInfoCurrent.value.size, lazyListState)
            }
        }


    }
}



@Composable
fun DataGridRow(modifier: Modifier = Modifier, data:SeawaterInformationByObservationPoint) {
    val obs_lay_Kor = when(data.obs_lay){ "1" -> "표층"; "2" -> "중층"; "3" -> "저층"; else ->"층" }
    Card(
        modifier = then(modifier).fillMaxWidth().height(46.dp).padding(0.dp),
        elevation = 0.dp,
        shape = RoundedCornerShape(1.dp),
        border = BorderStroke(width = 1.dp, color = Color.LightGray.copy(alpha = 0.2f)),
        backgroundColor = MaterialTheme.colors.surface
    ) {


        Row (
            modifier = then(modifier).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround

        ) {
            Text(data.obs_datetime, modifier = Modifier.fillMaxWidth(0.2f))
            Text(data.gru_nam, modifier = Modifier.fillMaxWidth(0.1f))
            Text(data.sta_nam_kor, modifier = Modifier.fillMaxWidth(0.1f))
            Text(data.sta_cde, modifier = Modifier.fillMaxWidth(0.1f))
            Text(obs_lay_Kor, modifier = Modifier.fillMaxWidth(0.1f))
            Text(data.wtr_tmp, modifier = Modifier.fillMaxWidth(0.1f))
            Text(data.lat.toString(), modifier = Modifier.fillMaxWidth(0.15f))
            Text(data.lon.toString(), modifier = Modifier.fillMaxWidth(0.15f))
        }
    }
}

@Composable
fun HeaderGridRow(modifier: Modifier = Modifier) {

    var gruNameImageVector: ImageVector by remember { mutableStateOf(Icons.Default.KeyboardArrowDown) }

    Card(
        modifier = then(modifier).fillMaxWidth().height(60.dp).padding(2.dp),
        elevation = 1.dp,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.surface
    ) {

        Row (
            modifier = then(modifier).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround

        ){
            Text ( "수집시간" , modifier = Modifier.fillMaxWidth(0.2f))

            Row (
                modifier = Modifier.fillMaxWidth(0.1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text ( "해역" )
                IconButton(
                    onClick = {
                        gruNameImageVector = if (gruNameImageVector.equals(Icons.Default.KeyboardArrowDown)) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        }
                    }
                ) {
                    Icon(gruNameImageVector, contentDescription = "Sort")
                }
            }

            Text ( "관측지점" , modifier = Modifier.fillMaxWidth(0.1f))



            Text ( "지점코드" , modifier = Modifier.fillMaxWidth(0.1f))
            Text ( "수심" , modifier = Modifier.fillMaxWidth(0.1f))
            Text ( "수온 °C" , modifier = Modifier.fillMaxWidth(0.1f))
            Text ( "경도", modifier = Modifier.fillMaxWidth(0.15f))
            Text ( "위도" , modifier = Modifier.fillMaxWidth(0.15f))

        }

    }

}

@Composable
fun FooterGridRow(modifier: Modifier = Modifier, dataCnt:Int, listState: LazyListState) {

    val coroutineScope = rememberCoroutineScope()
    val pageCount: MutableState<Int> = remember { mutableStateOf(20) }


    LaunchedEffect(pageCount){

    }

    Card(
        modifier = then(modifier).fillMaxWidth().height(60.dp).padding(2.dp),
        elevation = 1.dp,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(width = 1.dp, color = Color.LightGray.copy(alpha = 0.6f)),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Row (
            modifier = then(modifier).fillMaxWidth().padding(end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center

        ){

            IconButton(
                modifier = Modifier.width(100.dp),
                enabled =  listState.firstVisibleItemIndex != 0,
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Goto First Page")
            }

            Text ( "Total Count : ${dataCnt}", )
            IconButton(
                modifier = Modifier.width(100.dp),
                enabled = listState.canScrollForward == true,
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(dataCnt-1)
                    }
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Goto Last Page")
            }


        }
    }
}

@Composable
fun MinimalDropdownMenu(pageCount: MutableState<Int>) {

    var expanded by remember { mutableStateOf(false) }
 //   var pageCount by remember { mutableStateOf("20") }
    val pageCountList = listOf("20", "100", "1000")



    Box(
        modifier = Modifier.fillMaxHeight(0.7f).width(80.dp)
            .border( BorderStroke(width = 1.dp, color = Color.LightGray.copy(alpha = 0.6f)), RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center,

    ){
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(pageCount.value.toString())

            IconButton(
                onClick = { expanded = !expanded }
            ) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Page Size")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {

                pageCountList.forEach {
                    DropdownMenuItem(onClick = {
                        pageCount.value = it.toInt()
                        expanded = false
                    }){
                        Text(text = it)
                    }
                }

            }


        }

    }


}
