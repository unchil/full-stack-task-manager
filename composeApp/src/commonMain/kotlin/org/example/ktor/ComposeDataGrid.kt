package org.example.ktor

//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.then
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ComposeDataGrid(
    modifier:Modifier = Modifier,
    columnNames:List<String>,
    data:List<List<Any>>,
){

    val coroutineScope = rememberCoroutineScope()

    val columnInfo = remember { mutableStateOf(makeColInfo(columnNames, data.first() as List<Any>) ) }

    val presentData: MutableState<List<Any>>  =  remember { mutableStateOf(data) }

    val onSortOrder:(columnName:String, colInfo:ColumnInfo)->Unit = { columnName, columnInfo ->

        columnInfo.sortOrder = when(columnInfo.sortOrder){
            0 -> 1
            1 -> -1
            else -> 0
        }

        when(columnInfo.columnType){
            "String" -> {
                when(columnInfo.sortOrder){
                    1 -> presentData.value = data.sortedBy { (it[columnNames.indexOf(columnName)] as String) }
                    -1 -> presentData.value =  data.sortedByDescending { (it[columnNames.indexOf(columnName)] as String) }
                    else -> presentData.value = data
                }
            }
            "Double" -> {
                when(columnInfo.sortOrder){
                    1 -> presentData.value =  data.sortedBy { (it[columnNames.indexOf(columnName)] as Double) }
                    -1 -> presentData.value =  data.sortedByDescending { (it[columnNames.indexOf(columnName)] as Double) }
                    else -> presentData.value =  data
                }
            }
            else -> {
                presentData.value = data
            }
        }

    }

    val onFilter:(columnName:String, searchText:String) -> Unit = { columnName, searchText  ->
        presentData.value =  data.filter {
            it[columnNames.indexOf(columnName)].toString().contains(searchText)
        }
    }

    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)

    val onRefresh:()-> Unit = {
        coroutineScope.launch {
            presentData.value = data
            lazyListState.animateScrollToItem(0)
        }
    }

    Scaffold(
        modifier = then(modifier).fillMaxSize()
            .padding(0.dp).border(BorderStroke(width = 1.dp, color = Color.LightGray), RoundedCornerShape(6.dp))
            .background(color = Color.LightGray),
        topBar = {
            ComposeDataGridHeader(
                modifier = Modifier.fillMaxWidth(),
                columnInfo,
                onSortOrder,
                onFilter
            )
        },
        bottomBar = {
            ComposeDataGridFooter(
                modifier = Modifier.fillMaxWidth(),
                lazyListState ,
                presentData.value.size,
                onRefresh
            )
        },
    ){
        LazyColumn (
            modifier =  Modifier.fillMaxSize() .padding(it)

               ,
            state = lazyListState,
            contentPadding = PaddingValues(1.dp),
            userScrollEnabled = true
        ){

            items(presentData.value.size){

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .border(BorderStroke(width = 1.dp, color = Color.LightGray.copy(alpha = 0.2f))),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text((it+1).toString(),Modifier.width( 30.dp), textAlign= TextAlign.Center)
                    ComposeDataGridRow( columnInfo, presentData.value[it] as List<Any?>)
                }
            }

        }
    }

}


@Composable
fun ComposeDataGridRow(  columnInfo:MutableState<Map<String, ColumnInfo>>, data:List<Any?>) {

    val density = LocalDensity.current.density
    var widthInDp by remember { mutableStateOf(0.dp) }

    Row (
        modifier = Modifier.fillMaxWidth().height(50.dp)
            .onGloballyPositioned { layoutResult ->
                widthInDp =  ( layoutResult.size.width / density).dp
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        data.forEach {
            Row(
                modifier = Modifier.width(widthInDp/ columnInfo.value.size) ,
                horizontalArrangement = Arrangement.Center
            ) {
                Text( it.toString())
            }
        }
    }

}



@Composable
fun ComposeDataGridHeader(
    modifier: Modifier = Modifier,
    columnInfo: MutableState<Map<String, ColumnInfo>>,
    onSortOrder:((String, ColumnInfo) -> Unit)? = null,
    onFilter:((String, String) -> Unit)? = null,
) {

    Row (
        modifier =  then(modifier).fillMaxWidth().height(60.dp)
            .padding(0.dp)
            .border(BorderStroke(width = 1.dp, color = Color.LightGray), RoundedCornerShape(6.dp))
            .background( MaterialTheme.colors.surface),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ){
        Text("", Modifier.width( 30.dp))

        ComposeColumnRow(columnInfo, onSortOrder, onFilter)
    }

}

@Composable
fun ComposeColumnRow(
    columnInfo: MutableState<Map<String, ColumnInfo>>,
    onSortOrder:((String, ColumnInfo) -> Unit)? = null,
    onFilter:((String, String) -> Unit)? = null,
){
    val density = LocalDensity.current.density
    var widthInDp by remember { mutableStateOf(0.dp) }

    Row (
        modifier = Modifier.fillMaxWidth().height(60.dp)
            .onGloballyPositioned { layoutResult ->
                widthInDp =  ( layoutResult.size.width / density).dp
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        columnInfo.value.forEach { (key, value) ->
            Row(
                modifier = Modifier.width(widthInDp / (columnInfo.value.size)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = { onSortOrder?.invoke(key, value) }) { Text(key) }
                FilterMenu(key, onFilter)

            }

            Divider(
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .height(40.dp).width(1.dp)
                ,
                color = Color.LightGray,
            )

        }
    }
}


@Composable
fun ComposeDataGridFooter(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    dataCnt: Int,
    onRefresh:(()->Unit)? = null,
) {
    val coroutineScope = rememberCoroutineScope()

    Row (
        modifier = then(modifier).fillMaxWidth()
            .padding(horizontal = 0.dp)
            .background(MaterialTheme.colors.surface)
            .border(BorderStroke(width = 1.dp, color = Color.LightGray), RoundedCornerShape(6.dp))
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center

    ){

        IconButton(
            modifier = Modifier,
            enabled =  lazyListState.firstVisibleItemIndex != 0,
            onClick = { coroutineScope.launch { lazyListState.animateScrollToItem(0)  }  }
        ) {  Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Goto First Page") }

        Text ( "Total Count : $dataCnt" )

        IconButton(
            modifier = Modifier,
            enabled = lazyListState.canScrollForward,
            onClick = {  coroutineScope.launch { lazyListState.animateScrollToItem(dataCnt-1) } }
        ) { Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Goto Last Page") }

        IconButton(
            onClick = { coroutineScope.launch { onRefresh?.invoke() } }
        ) {  Icon(Icons.Default.Refresh, contentDescription = "Refresh")  }

    }

}



@Composable
fun FilterMenu(columnName:String, onFilter: ((String, String)-> Unit)? = null ) {
    var expanded by remember { mutableStateOf(false) }
    val filterText = remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val onSearch: () -> Unit = {
        onFilter?.invoke(columnName, filterText.value)
        expanded = false
        filterText.value = ""
    }

    LaunchedEffect(isPressed){
        if(isPressed) {
            onSearch()
        }
    }


    Box(  contentAlignment = Alignment.Center, ){

        IconButton( onClick = {  expanded = !expanded } ){
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Filter")  }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                filterText.value = ""
            }
        ) {
            OutlinedTextField(
                modifier = Modifier.padding(horizontal = 8.dp).onKeyEvent { event ->
                    if (event.key.equals(Key.Enter) && event.type.equals(KeyEventType.KeyUp) ) {
                        onSearch()
                        true
                    }else{
                        false
                    }
                }.onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
                value = filterText.value,
                onValueChange = {
                    filterText.value = it
                },
                label = { Text("Filter...")  },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            onSearch()
                        },
                        interactionSource = interactionSource,
                        enabled = isFocused,
                    ) {
                        Icon(Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (isFocused) { Color(128,65,217)} else Color.LightGray
                        )
                    }
                },
                singleLine = true,
            )
        }
    }
}

data class ColumnInfo(val columnType: String, var sortOrder: Int)

fun makeColInfo(columnNames: List<String>, firstData: List<Any>): Map<String, ColumnInfo> {
    val colInfo = mutableMapOf<String, ColumnInfo>()

    columnNames.forEachIndexed{ index, columnName ->
        colInfo.put(columnName, ColumnInfo(firstData[index]::class.simpleName.toString(), 0))
    }
    return colInfo
}