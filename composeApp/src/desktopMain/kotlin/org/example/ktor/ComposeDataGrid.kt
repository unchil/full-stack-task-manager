package org.example.ktor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@Composable
fun ComposeDataGrid(
    columnNames:List<String>,
    data:List<List<Any>>,
){

    val columnInfo = remember { mutableStateOf(makeColInfo(columnNames, data.first() as List<Any>) ) }

    val presentData: MutableState<List<Any>> =  mutableStateOf(data)

    val onSortOrder:(columnName:String, colInfo:ColumnInfo)->Unit = { columnName, columnInfo ->

    }

    val onFilter:(columnName:String, colInfo:ColumnInfo) -> Unit = { columnName, columnInfo  ->

    }

    val onRefresh:()-> Unit = {
        presentData.value = data
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(10.dp).background(color = Color.LightGray),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment= Alignment.CenterHorizontally,

    ) {
        val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
        ComposeDataGridHeader( modifier = Modifier.fillMaxWidth(), columnInfo, onSortOrder, onFilter)
        ComposeDataGridRows( modifier= Modifier.fillMaxWidth(), lazyListState,  presentData.value)
        ComposeDataGridFooter(  modifier = Modifier.fillMaxWidth(), lazyListState ,  presentData.value.size,  onRefresh)
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
    onSortOrder:((String, ColumnInfo) -> Unit)? = null,
    onFilter:((String, ColumnInfo) -> Unit)? = null,
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
                    IconButton( onClick = { onSortOrder?.invoke(key, value) } ) { Text ( key) }
                    FilterMenu{ onFilter?.invoke(key, value)}
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
    onRefresh:(()->Unit)? = null,
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
                        onRefresh?.invoke()
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

data class ColumnInfo(val columnType: String, val sortOrder: Int, val filterText: String)

fun makeColInfo(columnNames: List<String>, firstData: List<Any>): Map<String, ColumnInfo> {
    val colInfo = mutableMapOf<String, ColumnInfo>()

    columnNames.forEachIndexed{ index, columnName ->
        colInfo.put(columnName, ColumnInfo(firstData[index]::class.simpleName.toString(), 0, ""))
    }
    return colInfo
}