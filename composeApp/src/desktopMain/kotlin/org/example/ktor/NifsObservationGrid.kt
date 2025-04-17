package org.example.ktor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
    val coroutineScope = rememberCoroutineScope()

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

    LaunchedEffect (key1= seaWaterInfoCurrent.value){
        lazyListState.animateScrollToItem(0)
    }


    val sortOrderHandler:(colunmName:String, sortOrder: String)->Unit = { columnName, sortOrder ->
        coroutineScope.launch {
            viewModel.onEvent( NifsSeaWaterInfoCurrentViewModel.Event.SortOrder(columnName, sortOrder) )

        }
    }

    MaterialTheme {

        Box(
            modifier = Modifier.size(width = 1300.dp, height = 530.dp).padding(bottom = 20.dp)
                .border(
                    border = BorderStroke(width = 1.dp, color = Color.LightGray.copy(alpha = 0.5f)  ),
                    shape = RoundedCornerShape(2.dp)
                )
        ){
            Column(modifier = then(modifier)) {

                HeaderGridRow(modifier = Modifier.fillMaxWidth(), sortOrderHandler)


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


            Row( modifier = Modifier.fillMaxWidth(0.16f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(data.obs_datetime )
            }


            Row( modifier = Modifier.fillMaxWidth(0.1f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(data.gru_nam )
            }


            Row( modifier = Modifier.fillMaxWidth(0.1f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(data.sta_nam_kor )
            }


            Row( modifier = Modifier.fillMaxWidth(0.1f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(data.sta_cde )
            }

            Row( modifier = Modifier.fillMaxWidth(0.1f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(obs_lay_Kor)
            }


            Row( modifier = Modifier.fillMaxWidth(0.1f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(data.wtr_tmp)
            }

            Row( modifier = Modifier.fillMaxWidth(0.15f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(data.lat.toString())
            }

            Row( modifier = Modifier.fillMaxWidth(0.15f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(data.lon.toString())
            }
        }
    }
}

@Composable
fun HeaderGridRow(modifier: Modifier = Modifier, sortOrderHandler:(columnName:String, orderBy: String)->Unit) {

    val sortOrderGruNam = remember { mutableStateOf(0) }
    val sortOrderStaNam = remember { mutableStateOf(0) }
    val sortOrderWtrTmp = remember { mutableStateOf(0) }
    val sortOrderLat = remember { mutableStateOf(0) }
    val sortOrderLon = remember { mutableStateOf(0) }
    val sortOrderDateTime = remember { mutableStateOf(0) }
    val sortOrderObsLay = remember { mutableStateOf(0) }
    val sortOrderStaCde = remember { mutableStateOf(0) }

    val setSortOrder:(colunmName:String, value:Int)->Unit = { columnName, value ->
        when(columnName){
            "gru_nam" -> sortOrderGruNam.value = value
            "sta_nam_kor" -> sortOrderStaNam.value = value
            "wtr_tmp" -> sortOrderWtrTmp.value = value
            "lat_tmp" -> sortOrderLat.value = value
            "lon_tmp" -> sortOrderLon.value = value
            "obs_datetime" ->sortOrderDateTime.value = value
            "obs_lay" -> sortOrderObsLay.value = value
            "sta_cde" -> sortOrderStaCde.value = value
            else -> { 0}
        }
    }
    val onClickEvent:(colunmName:String)->Unit =  { columnName ->
        val sortOrder = when(columnName){
            "gru_nam" -> sortOrderGruNam.value
            "sta_nam_kor" -> sortOrderStaNam.value
            "wtr_tmp" -> sortOrderWtrTmp.value
            "lat_tmp" -> sortOrderLat.value
            "lon_tmp" -> sortOrderLon.value
            "obs_datetime" ->sortOrderDateTime.value
            "obs_lay" -> sortOrderObsLay.value
            "sta_cde" -> sortOrderStaCde.value
            else -> { 0}
        }

        when(sortOrder) {
            0 -> {
                sortOrderHandler(columnName, "DESC")
                setSortOrder(columnName, 1)
            }
            1 -> {
                sortOrderHandler(columnName, "ASC")
                setSortOrder(columnName, 2)
            }
            2 -> {
                sortOrderHandler(columnName, "ORG")
                setSortOrder(columnName, 0)
            }
        }

    }



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

            IconButton(
                modifier = Modifier.fillMaxWidth(0.16f),
                onClick = { onClickEvent("obs_datetime") }
            ) {
                Row( modifier = then(modifier).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text ( "수집시간")
                }
            }



            IconButton(
                modifier = Modifier.fillMaxWidth(0.1f),
                onClick = { onClickEvent("gru_nam") }
            ) {
                Row( modifier = then(modifier).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text ( "해역")
                }
            }



            IconButton(
                modifier = Modifier.fillMaxWidth(0.1f),
                onClick = { onClickEvent("sta_nam_kor") }
            ) {
                Row( modifier = then(modifier).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text ( "관측지점")
                }
            }


            IconButton(
                modifier = Modifier.fillMaxWidth(0.1f),
                onClick = { onClickEvent("sta_cde") }
            ) {
                Row( modifier = then(modifier).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text ( "지점코드")
                }
            }


            IconButton(
                modifier = Modifier.fillMaxWidth(0.1f),
                onClick = { onClickEvent("obs_lay") }
            ) {
                Row( modifier = then(modifier).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text ( "수심")
                }
            }




            IconButton(
                modifier = Modifier.fillMaxWidth(0.1f),
                onClick = { onClickEvent("wtr_tmp") }
            ) {
                Row( modifier = then(modifier).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text ( "수온 °C")
                }
            }




            IconButton(
                modifier = Modifier.fillMaxWidth(0.15f),
                onClick = {
                //onClickEvent("lon")
                }
            ) {
                Row( modifier = then(modifier).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text ( "경도")
                }
            }



            IconButton(
                modifier = Modifier.fillMaxWidth(0.15f),
                onClick = {
                //onClickEvent("lat")
                }
            ) {
                Row( modifier = then(modifier).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text ( "위도")
                }
            }

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
