package org.example.ktor

//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import kotlin.math.max

@Composable
fun ComposeDataGrid(
    modifier:Modifier = Modifier,
    columnNames:List<String>,
    data:List<List<Any?>>,
){

    val coroutineScope = rememberCoroutineScope()

    val columnInfo = remember { mutableStateOf(makeColInfo(columnNames, data) ) }

    val presentData: MutableState<List<Any?>>  =  remember { mutableStateOf(data) }

    val onSortOrder:(columnName:String, colInfo:ColumnInfo)->Unit = { columnName, colInfo ->

        colInfo.sortOrder = when(colInfo.sortOrder){
            0 -> 1
            1 -> -1
            else -> 0
        }

        if(colInfo.isContainNull){

            presentData.value = data

        }else{
            when(colInfo.columnType){
                "String" -> {
                    when(colInfo.sortOrder){
                        1 -> presentData.value = data.sortedBy { (it[columnNames.indexOf(columnName)] as String) }
                        -1 -> presentData.value =  data.sortedByDescending { (it[columnNames.indexOf(columnName)] as String) }
                        else -> presentData.value = data
                    }
                }
                "Double" -> {
                    when(colInfo.sortOrder){
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
                    Text((it+1).toString(),Modifier.width( 40.dp), textAlign= TextAlign.Center)
                    ComposeDataGridRow( columnInfo, presentData.value[it] as List<Any?>)
                }
            }

        }
    }

}


@Composable
fun ComposeDataGridRow(  columnInfo:MutableState<Map<String, ColumnInfo>>, data:List<Any?>) {

    val columnInfoList = columnInfo.value.toList()

    Row (
        modifier = Modifier.fillMaxWidth().height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {

        columnInfoList.forEachIndexed { index, (key, columnInfo) ->
            Row(
                modifier = Modifier.weight(columnInfo.widthWeigth.value),
                horizontalArrangement = Arrangement.Center
            ) {
                Text( data[index].toString())
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
        Text("", Modifier.width( 40.dp))

        ComposeColumnRow( columnInfo, onSortOrder, onFilter)
    }

}

@Composable
fun ComposeColumnRow(
    columnInfo: MutableState<Map<String, ColumnInfo>>,
    onSortOrder:((String, ColumnInfo) -> Unit)? = null,
    onFilter:((String, String) -> Unit)? = null, ) {

    require(columnInfo.value.size >= 2) { "column must be at least 2" }

    val columnCount = columnInfo.value.size
    val columnInfoList = columnInfo.value.toList()

    val dividerPositions = remember { MutableList(columnCount - 1) { 0.dp } }
    val density = LocalDensity.current.density
    var rowWidthInDp by remember { mutableStateOf(0.dp) }
    val dividerThickness = 2.dp
    val interactionSources = remember { List(columnCount - 1) { MutableInteractionSource() } }
    val totalWidth = rowWidthInDp - (dividerThickness * (columnCount - 1))
    val draggableStates = (0 until columnCount - 1).map { index ->

        rememberDraggableState { delta ->

            val newPositionDp = ( dividerPositions[index] + (delta/density).dp  ).coerceIn(0.dp, totalWidth)

            dividerPositions[index] = newPositionDp

            val newWeightBefore = (newPositionDp / totalWidth)
            val newWeightAfter = 1f - newWeightBefore

            var oldSumBefore = 0f

            for (i in 0 until index + 1){
                oldSumBefore += columnInfoList[i].second.widthWeigth.value
            }
            val oldSumAfter = 1f - oldSumBefore

            // Standard
            columnInfoList[index].second.widthWeigth.value = (newWeightBefore / oldSumBefore) * columnInfoList[index].second.widthWeigth.value

            // After
            for (i in index + 1 until columnCount) {
                columnInfoList[i].second.widthWeigth.value = (newWeightAfter / oldSumAfter) *columnInfoList[i].second.widthWeigth.value
            }

            // Ensure weights don't go below a minimum value (e.g., 0.1f)
            for (i in 0 until columnCount) {
                columnInfoList[i].second.widthWeigth.value = max(columnInfoList[i].second.widthWeigth.value, 0.01f)
            }


            var sum = 0f
            columnInfoList.forEach {
                sum += it.second.widthWeigth.value
            }

            // Normalize weights to ensure they sum to 1
            columnInfoList.forEach {
                it.second.widthWeigth.value /= sum
            }

        }

    }

    LaunchedEffect(rowWidthInDp) {
        if (rowWidthInDp > 0.dp) {
            val initialPosition = (rowWidthInDp / columnCount)
            for (i in 0 until columnCount - 1) {
                dividerPositions[i] = initialPosition * (i + 1) - (dividerThickness * (i + 1) / 2)
            }
        }
    }



    Row(
        Modifier
            .fillMaxSize()
            .onGloballyPositioned { layoutResult ->
                rowWidthInDp = (layoutResult.size.width / density).dp
            }
            //     .border(BorderStroke(width = 1.dp, color = Color.LightGray), RoundedCornerShape(6.dp))
            .background( MaterialTheme.colors.surface),
        verticalAlignment = Alignment.CenterVertically,
    ) {


        columnInfoList.forEachIndexed { index, (key, columnInfo) ->

            Row(
                modifier = Modifier.weight(columnInfo.widthWeigth.value),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = { onSortOrder?.invoke(key, columnInfo) }) { Text(key) }
                FilterMenu(key, onFilter)

            }

            if ( index < columnCount - 1) {
                Divider(
                    Modifier
                        .width(dividerThickness)
                        .height(40.dp).width(1.dp)
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = draggableStates[index],
                            interactionSource = interactionSources[index]
                        ),
                    color = Color.LightGray,
                    thickness = dividerThickness
                )
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

data class ColumnInfo(val columnType: String, var sortOrder: Int, val widthWeigth: MutableState<Float>, val isContainNull:Boolean = false)

fun makeColInfo(columnNames: List<String>, data: List<List<Any?>>): Map<String, ColumnInfo> {

    val isContainNull = columnNames.map { false }.toMutableList()
    data.forEach{
        it.forEachIndexed {  index, any ->
            if(!isContainNull[index].equals(null)){
                isContainNull[index] = (any == null)
            }
        }
    }

    val colInfo = mutableMapOf<String, ColumnInfo>()

    columnNames.forEachIndexed{ index, columnName ->
        colInfo.put(
            columnName,
            ColumnInfo(
                if (isContainNull[index]) {"String"} else  {data.first()[index]!!::class.simpleName.toString()},
                0,
                mutableStateOf(1f / columnNames.size),
                isContainNull[index]
            )
        )
    }
    return colInfo
}


