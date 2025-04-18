package org.example.ktor


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.then
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
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

    val onFilterHandler:(columnName:String, searchText:String) -> Unit = { columnName, searchText ->
        coroutineScope.launch {
            viewModel.onEvent(NifsSeaWaterInfoCurrentViewModel.Event.SearchData(columnName, searchText))
        }
    }

    val onRefreshHandler:()-> Unit = {
        coroutineScope.launch {
            viewModel.onEvent(NifsSeaWaterInfoCurrentViewModel.Event.ObservationRefresh(DATA_DIVISION.current))
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

                HeaderGridRow(modifier = Modifier.fillMaxWidth(), sortOrderHandler, onFilterHandler)


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

                FooterGridRow(
                    modifier = Modifier.fillMaxWidth(),
                    seaWaterInfoCurrent.value.size,
                    lazyListState,
                    onRefreshHandler
                )
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
fun HeaderGridRow(
    modifier: Modifier = Modifier,
    sortOrderHandler:(columnName:String, orderBy: String)->Unit,
    onFilterHandler:(columnName:String, searchText: String)->Unit
) {

    val sortOrderGruNam = remember { mutableStateOf(0) }
    val sortOrderStaNam = remember { mutableStateOf(0) }
    val sortOrderWtrTmp = remember { mutableStateOf(0) }
    val sortOrderLat = remember { mutableStateOf(0) }
    val sortOrderLon = remember { mutableStateOf(0) }
    val sortOrderDateTime = remember { mutableStateOf(0) }
    val sortOrderObsLay = remember { mutableStateOf(0) }
    val sortOrderStaCde = remember { mutableStateOf(0) }

    val setSortOrder:(columnName:String, value:Int)->Unit = { columnName, value ->
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
    val onClickEvent:(columnName:String)->Unit =  { columnName ->
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


            Row ( modifier = Modifier.fillMaxWidth(0.1f),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(onClick = {onClickEvent("gru_nam")}) {Text ( "해역")}
                FilterBox{ onFilterHandler("gru_nam", it)}
            }



            Row ( modifier = Modifier.fillMaxWidth(0.1f),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(onClick = {onClickEvent("sta_nam_kor")}) {Text ( "관측지점")}
                FilterBox{ onFilterHandler("sta_nam_kor", it)}
            }


            Row ( modifier = Modifier.fillMaxWidth(0.1f),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(onClick = {onClickEvent("sta_cde")}) {Text ( "지점코드")}
                FilterBox{ onFilterHandler("sta_cde", it)}
            }



            Row ( modifier = Modifier.fillMaxWidth(0.1f),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(onClick = {onClickEvent("obs_lay")}) {Text ( "수심")}

                FilterBox{
                    onFilterHandler(
                        "obs_lay",
                        when(it) {
                            "표층" -> "1"
                            "중층" -> "2"
                            "저층" -> "3"
                            else ->"1"
                        }
                    )
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


            Text ( "경도", modifier = Modifier.fillMaxWidth(0.15f))
            Text ( "위도", modifier = Modifier.fillMaxWidth(0.15f))



        }

    }

}

@Composable
fun FooterGridRow(
    modifier: Modifier = Modifier,
    dataCnt: Int,
    listState: LazyListState,
    onRefresh:()->Unit
) {

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
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Goto First Page")
            }


            Text ( "Total Count : ${dataCnt}",Modifier.width(140.dp) )

            IconButton(
                onClick = {
                    coroutineScope.launch {
                        onRefresh()
                    }
                }
            ) {  Icon(Icons.Default.Refresh, contentDescription = "Refresh")  }


            IconButton(
                modifier = Modifier.width(100.dp),
                enabled = listState.canScrollForward == true,
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(dataCnt-1)
                    }
                }
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Goto Last Page")
            }


        }
    }
}

@Composable
fun FilterBox( onFilter: ((String)-> Unit)? = null ) {

    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val filterText = remember { mutableStateOf("") }

    Box(  contentAlignment = Alignment.Center, ){
        IconButton(
            onClick = { expanded = !expanded }
        ) {
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Filter")
        }

        DropdownMenu(
            modifier =  Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    focusManager.clearFocus()
                    expanded = false
                    onFilter?.invoke(filterText.value.trim())
                    filterText.value = ""
                },
            ),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                filterText.value = ""
            }
        ) {

            OutlinedTextField(
                modifier = Modifier.padding(horizontal = 8.dp),
                value = filterText.value,
                onValueChange = {
                    filterText.value = it
                },
                label = { Text("Filter...")  },
                leadingIcon = {Icon(Icons.Default.Search, contentDescription = "Search")},
                singleLine = true,
            )

        }


    }


}

