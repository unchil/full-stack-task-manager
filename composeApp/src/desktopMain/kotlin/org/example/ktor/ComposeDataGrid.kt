package org.example.ktor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.ktor.data.DATA_DIVISION


@Composable
fun NifsSeaWaterInfoDataGrid() {

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

    val seaWaterInfoCurrent = viewModel._gridDataStateFlow.collectAsState()
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)


    LaunchedEffect (key1= seaWaterInfoCurrent.value){
        lazyListState.animateScrollToItem(0)
    }

    val sortOrder:(columnName:String)->Unit = { columnName ->
        coroutineScope.launch {

        }
    }

    val onFilter:(columnName:String) -> Unit = { columnName ->
        coroutineScope.launch {

        }
    }

    val onRefresh:()-> Unit = {
        coroutineScope.launch {

        }
    }


    if(seaWaterInfoCurrent.value.isNotEmpty()){

        val columnNames = listOf<String>("수집시간", "해역", "관측지점", "지점코드", "수심", "수온", "경도", "위도")
        val firstData = seaWaterInfoCurrent.value.first().toList()
        val columnInfo = remember { mutableStateOf(makeColInfo(columnNames, firstData)) }
        val data = seaWaterInfoCurrent.value.map { it.toList() }

        ComposeDataGrid( colInfo = columnInfo, data = data,sortOrder, onFilter = onFilter, onRefresh = onRefresh )

    }
}


@Composable
fun ComposeDataGrid(
    colInfo:MutableState<Map<String, ColumnInfo>> = remember { mutableStateOf(emptyMap()) },
    data:List<Any?>,
    onSortOrder:(String) -> Unit,
    onFilter:(String) -> Unit,
    onRefresh:() -> Unit
){
    Column(
        modifier = Modifier.fillMaxSize().padding(10.dp).background(color = Color.LightGray),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment= Alignment.CenterHorizontally,

    ) {
        val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
        ComposeDataGridHeader( modifier = Modifier.fillMaxWidth(), colInfo, onSortOrder, onFilter)
        ComposeDataGridRows( modifier= Modifier.fillMaxWidth(), lazyListState,  data)
        ComposeDataGridFooter(  modifier = Modifier.fillMaxWidth(), lazyListState ,  data.size,  onRefresh)
    }
}


@Composable
fun ComposeDataGridRows(modifier:Modifier, lazyListState: LazyListState,  data:List<Any?>) {
    LazyColumn (
        modifier =  Modifier.fillMaxWidth().height((800-60-60-42).dp),
        state = lazyListState,
        contentPadding = PaddingValues(1.dp),
        userScrollEnabled = true
    ){
        items(data.size){
            ComposeDataGridRow( data[it] as List<Any?>)
        }
    }
}

@Composable
fun ComposeDataGridRow(  data:List<Any?>) {

    Card(
        modifier = Modifier.fillMaxWidth().height(40.dp),
        elevation = 0.dp,
        shape = RoundedCornerShape(1.dp),
        border = BorderStroke(width = 1.dp, color = Color.LightGray.copy(alpha = 0.2f)),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            data.forEach {
                Row(
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(it.toString())
                }
            }
        }

    }
}



@Composable
fun ComposeDataGridHeader(
    modifier: Modifier = Modifier,
    columnInfo: MutableState<Map<String, ColumnInfo>>,
    onSortOrder:(String) -> Unit,
    onFilter:(String) -> Unit
) {
    Card(
        modifier =  Modifier.fillMaxWidth().height(60.dp),
        elevation = 0.dp,
        shape = RoundedCornerShape(2.dp),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ){

            columnInfo.value.forEach { (key, value) ->
                Row (
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    IconButton( onClick = { onSortOrder(key) } ) { Text ( key) }
                    FilterMenu{ onFilter(key)}
                }

            }

        }
    }
}


@Composable
fun ComposeDataGridFooter(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    dataCnt: Int,
    onRefresh:()->Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier =  Modifier.fillMaxWidth().height(60.dp),
        elevation = 0.dp,
        shape = RoundedCornerShape(2.dp),
        border = BorderStroke(width = 1.dp, color = Color.LightGray.copy(alpha = 0.6f)),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Row (
            modifier = then(modifier).fillMaxWidth().padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center

        ){

            IconButton(
                modifier = Modifier.width(100.dp),
                enabled =  lazyListState.firstVisibleItemIndex != 0,
                onClick = {
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(0)
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
                enabled = lazyListState.canScrollForward == true,
                onClick = {
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(dataCnt-1)
                    }
                }
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Goto Last Page")
            }


        }
    }
}



@Composable
fun FilterMenu( onFilter: ((String)-> Unit)? = null ) {
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val filterText = remember { mutableStateOf("") }

    Box(  contentAlignment = Alignment.Center, ){
        IconButton( onClick = { expanded = !expanded }  ) {
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
