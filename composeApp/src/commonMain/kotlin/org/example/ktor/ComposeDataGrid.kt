package org.example.ktor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.then
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

    val makeColInfo: (columnNames: List<String>, data: List<List<Any?>>) -> List<ColumnInfo>  = { columnNames, data ->
        val isContainNull = columnNames.map { false }.toMutableList()
        data.forEach{
            it.forEachIndexed {  index, any ->
                if(!isContainNull[index].equals(null)){
                    isContainNull[index] = (any == null)
                }
            }
        }
        val colInfo = mutableListOf< ColumnInfo>()
        columnNames.forEachIndexed{ index, columnName ->
            colInfo.add(
                ColumnInfo(
                    columnName,
                    index,
                    data.first { it[index] != null }[index]?.let {  it::class.simpleName.toString() }?: "NULL",
                    mutableStateOf(0),
                    mutableStateOf(1f / columnNames.size),
                    isContainNull[index]
                )
            )
        }
        colInfo
    }
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val columnInfo = remember { mutableStateOf(makeColInfo(columnNames, data) ) }

    val enablePagingGrid = remember { mutableStateOf(false) }

    val presentData: MutableState<List<Any?>>  =  remember { mutableStateOf(data) }
    val pagingData: MutableState<List<Any?>>  =  remember { mutableStateOf(data) }
    val sortedIndexList = remember { mutableListOf<Int>() }

    val initSortOrder:()->Unit = {
        sortedIndexList.clear()
        columnInfo.value.forEach { it.sortOrder.value = 0 }
    }

    val startRowNum = remember {  mutableStateOf(0)}
    val currentPage = remember {  mutableStateOf(1)}
    val pageSize = remember {  mutableStateOf(20)}

    val initPageData:()->Unit = {

        currentPage.value = 1
        val lastPage = if( presentData.value.size <= pageSize.value ) 1 else { if( presentData.value.size % pageSize.value == 0 ){ presentData.value.size/pageSize.value } else { (presentData.value.size/pageSize.value) + 1 } }
        val endIndex =  if( currentPage.value == lastPage){ presentData.value.size } else{ (pageSize.value * currentPage.value) }


        val currentPageData = mutableListOf<List<Any?>>()
        for ( i in 0  until endIndex){
            currentPageData.add( presentData.value[i] as List<Any?>)
        }
        pagingData.value = currentPageData

        coroutineScope.launch {
            lazyListState.animateScrollToItem(0)
        }

    }

    LaunchedEffect(enablePagingGrid.value) {

        if(enablePagingGrid.value){
            currentPage.value = 1
            pageSize.value = 20
        }
        coroutineScope.launch {
            lazyListState.animateScrollToItem(0)
        }

    }

    val updateSortedIndexList:(colInfo:ColumnInfo)->Unit = {
        if(sortedIndexList.isEmpty() ){
            sortedIndexList.add(it.columnIndex)
        } else {
            if (it.sortOrder.value == 0){
                initSortOrder()
                presentData.value = data

            } else {
                if(sortedIndexList.contains(it.columnIndex)) {
                    sortedIndexList.remove(it.columnIndex)
                    sortedIndexList.add(it.columnIndex)
                } else {
                    sortedIndexList.add(it.columnIndex)
                }
            }
        }
    }

    val onMultiSortedOrder:(colInfo:ColumnInfo)->Unit = { colInfo ->

        updateSortedIndexList(colInfo)

        if(sortedIndexList.isNotEmpty() ){
            val firstSortOrder = columnInfo.value[sortedIndexList.first()].sortOrder.value
            val firstColumnType =  columnInfo.value[sortedIndexList.first()].columnType
            // String    "\u0000":NullAtBeginning (ASCII 코드 0),   "":NullAtEnd
            var comparator = when(firstSortOrder){
                1 -> {
                    when(firstColumnType){
                        "String" -> compareBy { it.getOrNull(sortedIndexList.first()) as? String ?: "" }
                        "Double" -> compareBy { it.getOrNull(sortedIndexList.first()) as? Double ?: Double.MAX_VALUE }
                        "Float" -> compareBy { it.getOrNull(sortedIndexList.first()) as? Float ?: Float.MAX_VALUE }
                        "Int" -> compareBy { it.getOrNull(sortedIndexList.first()) as? Int ?: Int.MAX_VALUE }
                        "Long" -> compareBy { it.getOrNull(sortedIndexList.first()) as? Long ?: Long.MAX_VALUE }
                        else ->   compareBy { it[sortedIndexList.first()] as String }
                    }
                }
                -1 -> {
                    when(firstColumnType){
                        "String" -> compareByDescending { it.getOrNull(sortedIndexList.first()) as? String ?: "" }
                        "Double" -> compareByDescending { it.getOrNull(sortedIndexList.first()) as? Double ?: Double.MIN_VALUE }
                        "Float" -> compareByDescending { it.getOrNull(sortedIndexList.first()) as? Float ?: Float.MIN_VALUE }
                        "Int" -> compareByDescending { it.getOrNull(sortedIndexList.first()) as? Int ?: Int.MIN_VALUE }
                        "Long" -> compareByDescending { it.getOrNull(sortedIndexList.first()) as? Long ?: Long.MIN_VALUE }
                        else ->  compareByDescending { it[sortedIndexList.first()] as String }
                    }
                }
                else ->  compareBy<List<Any?>> { it[sortedIndexList.first()] as String }
            }
            if(sortedIndexList.size > 1){
                for (i in 1 until sortedIndexList.size){
                    val sortOrder = columnInfo.value[sortedIndexList[i]].sortOrder.value
                    val columnType =  columnInfo.value[sortedIndexList[i]].columnType
                    // String    "\u0000":NullAtBeginning (ASCII 코드 0),   "":NullAtEnd
                    when(sortOrder){
                        1 -> {
                            when(columnType){
                                "String" -> {comparator = comparator.thenBy { it.getOrNull(sortedIndexList[i]) as? String ?: "" }}
                                "Double" -> {comparator = comparator.thenBy { it.getOrNull(sortedIndexList[i]) as? Double ?: Double.MAX_VALUE  }}
                                "Float" -> {comparator = comparator.thenBy { it.getOrNull(sortedIndexList[i]) as? Float ?: Float.MAX_VALUE }}
                                "Int" -> {comparator = comparator.thenBy { it.getOrNull(sortedIndexList[i]) as? Int ?: Int.MAX_VALUE }}
                                "Long" -> {comparator = comparator.thenBy { it.getOrNull(sortedIndexList[i]) as? Long ?: Long.MAX_VALUE}}
                            }
                        }
                        -1 -> {
                            when(columnType){
                                "String" -> {comparator = comparator.thenByDescending { it.getOrNull(sortedIndexList[i]) as? String ?: "" }}
                                "Double" -> {comparator = comparator.thenByDescending { it.getOrNull(sortedIndexList[i]) as? Double ?: Double.MIN_VALUE}}
                                "Float" -> {comparator = comparator.thenByDescending { it.getOrNull(sortedIndexList[i]) as? Float ?: Float.MIN_VALUE }}
                                "Int" -> {comparator = comparator.thenByDescending { it.getOrNull(sortedIndexList[i]) as? Int ?: Int.MIN_VALUE}}
                                "Long" -> {comparator = comparator.thenByDescending { it.getOrNull(sortedIndexList[i]) as? Long ?: Long.MIN_VALUE }}
                            }
                        }
                    }
                }
            }

           val data:List<List<Any?>> = presentData.value.filterIsInstance<List<Any?>>()
            presentData.value = data.sortedWith(comparator)
        } else{
            presentData.value = data
        }
        if(enablePagingGrid.value) {
            initPageData()
        }

    }

    val onFilter:(columnName:String, searchText:String, operator:String) -> Unit = { columnName, searchText, operator  ->
        presentData.value = when(operator){
                OperatorMenu.Operator.Contains.toString() ->
                    presentData.value.filter {
                        it as List<*>
                        it[columnNames.indexOf(columnName)].toString().contains(searchText)
                    }
                OperatorMenu.Operator.DoseNotContains.toString() ->
                    presentData.value.filter {
                        it as List<*>
                        it[columnNames.indexOf(columnName)].toString().contains(searchText).not()
                    }
                OperatorMenu.Operator.Equals.toString() ->
                    presentData.value.filter {
                        it as List<*>
                        it[columnNames.indexOf(columnName)].toString().equals(searchText)
                    }
                OperatorMenu.Operator.DoseNotEquals.toString() ->
                    presentData.value.filter {
                        it as List<*>
                        it[columnNames.indexOf(columnName)].toString().equals(searchText).not()
                    }
                OperatorMenu.Operator.BeginsWith.toString() ->
                    presentData.value.filter {
                        it as List<*>
                        it[columnNames.indexOf(columnName)].toString().startsWith(searchText)
                    }
                OperatorMenu.Operator.EndsWith.toString() ->
                    presentData.value.filter {
                        it as List<*>
                        it[columnNames.indexOf(columnName)].toString().endsWith(searchText)
                    }
                OperatorMenu.Operator.Blank.toString() ->
                    presentData.value.filter {
                        it as List<*>
                        it[columnNames.indexOf(columnName)].toString().isBlank()
                    }
                OperatorMenu.Operator.NotBlank.toString() ->
                    presentData.value.filter {
                        it as List<*>
                        it[columnNames.indexOf(columnName)].toString().isNotBlank()
                    }
                OperatorMenu.Operator.Null.toString() ->
                    presentData.value.filter {
                        it as List<*>
                        it[columnNames.indexOf(columnName)] == null
                    }
                OperatorMenu.Operator.NotNull.toString() ->
                    presentData.value.filter {
                        it as List<*>
                        it[columnNames.indexOf(columnName)] != null
                    }
                else -> {
                    presentData.value
                }
            }

        if(enablePagingGrid.value) {
            initPageData()
        }
    }

    val onRefresh:()-> Unit = {
        presentData.value = data
        currentPage.value = 1
        initSortOrder()
        coroutineScope.launch {
            lazyListState.animateScrollToItem(0)
        }
    }

    val onPageChange:(Int, Int)->Unit = { startIndex, endIndex->
        startRowNum.value = startIndex
        val currentPageData = mutableListOf<List<Any?>>()
        for ( i in startIndex  until endIndex){
            currentPageData.add( presentData.value[i] as List<Any?>)
        }
        pagingData.value = currentPageData
        coroutineScope.launch {
            lazyListState.animateScrollToItem(0)
        }

  }


    Scaffold(
        modifier = then(modifier).fillMaxSize()
            .padding(0.dp)
            .border(
                BorderStroke(width = 1.dp, color = Color.LightGray),
                RoundedCornerShape(6.dp)
            ),
        topBar = {
            ComposeDataGridHeader(
                modifier = Modifier.fillMaxWidth(),
                columnInfo = columnInfo,
                onSortOrder = onMultiSortedOrder,
                onFilter = onFilter
            )
        },
        bottomBar = {
            if(enablePagingGrid.value) {
                ComposeDataGridFooter(currentPage, pageSize, presentData.value.size, onPageChange,)
            }
        },
    ){

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {

            LazyColumn (
                modifier = Modifier.fillMaxSize()
                    .padding(it),
                state = lazyListState,
                contentPadding = PaddingValues(1.dp),
                userScrollEnabled = true,
            ){
                items( if(enablePagingGrid.value) {pagingData.value.size} else {presentData.value.size}){
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .border(BorderStroke(width = 1.dp, color = Color.LightGray.copy(alpha = 0.2f))),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        // row number
                        Text(
                            text =  if(enablePagingGrid.value) {  (startRowNum.value + it +  1).toString() } else { (it + 1).toString() },
                            modifier = Modifier.width(40.dp),
                            textAlign = TextAlign.Center
                        )

                        ComposeDataGridRow(columnInfo, if(enablePagingGrid.value) {pagingData.value[it] as List<Any?>} else {presentData.value[it] as List<Any?>})
                    }
                }

            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ){
                ComposeDataGridFooter(
                    modifier = Modifier.width(360.dp).padding(bottom = if(enablePagingGrid.value) { 70.dp} else { 10.dp}),
                    lazyListState = lazyListState ,
                    dataCnt = if(enablePagingGrid.value) {pagingData.value.size} else {presentData.value.size},
                    enablePagingGrid = enablePagingGrid,
                    onRefresh = onRefresh
                )
            }

        }
    }

}


@Composable
fun ComposeDataGridRow(  columnInfo:MutableState<List< ColumnInfo>>, data:List<Any?>) {
    Row (
        modifier = Modifier.fillMaxWidth().height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        columnInfo.value.toList().forEachIndexed { index, columnInfo ->
            Row(
                modifier = Modifier.weight(columnInfo.widthWeigth.value),
                horizontalArrangement = Arrangement.Center
            ) {
                Text( data[index].toString() )
            }
        }
    }
}



@Composable
fun ComposeDataGridHeader(
    modifier: Modifier = Modifier,
    columnInfo: MutableState<List< ColumnInfo>>,
    onSortOrder:((ColumnInfo) -> Unit)? = null,
    onFilter:((String, String, String) -> Unit)? = null,
) {

    Row (
        modifier =  then(modifier).fillMaxWidth()
            .height(50.dp)
            .border(BorderStroke(width = 1.dp, color = Color.LightGray), RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ){

        // row number space
        Text("", Modifier.width( 40.dp))

        ComposeColumnRow( columnInfo, onSortOrder, onFilter)
    }

}

@Composable
fun ComposeColumnRow(
    columnInfoList: MutableState<List< ColumnInfo>>,
    onSortOrder:(( ColumnInfo) -> Unit)? = null,
    onFilter:((String, String, String) -> Unit)? = null, ) {

    require(columnInfoList.value.size >= 2) { "column must be at least 2" }

    val columnCount = columnInfoList.value.size
    val dividerPositions = remember { MutableList(columnCount - 1) { 0.dp } }
    val density = LocalDensity.current.density
    var rowWidthInDp by remember { mutableStateOf(0.dp) }


    val dividerThickness = 2.dp
    val totalWidth = rowWidthInDp - (dividerThickness * (columnCount - 1))
    val draggableStates = (0 until columnCount - 1).map { index ->
        rememberDraggableState { delta ->
            val newPositionDp = ( dividerPositions[index] + (delta/density).dp  ).coerceIn(0.dp, totalWidth)
            dividerPositions[index] = newPositionDp
            val newWeightBefore = (newPositionDp / totalWidth)
            val newWeightAfter = 1f - newWeightBefore
            var oldSumBefore = 0f
            for (i in 0 until index + 1){
                oldSumBefore += columnInfoList.value[i].widthWeigth.value
            }
            val oldSumAfter = 1f - oldSumBefore
            // Standard
            columnInfoList.value[index].widthWeigth.value = (newWeightBefore / oldSumBefore) * columnInfoList.value[index].widthWeigth.value
            // After
            for (i in index + 1 until columnCount) {
                columnInfoList.value[i].widthWeigth.value = (newWeightAfter / oldSumAfter) *columnInfoList.value[i].widthWeigth.value
            }
            // Ensure weights don't go below a minimum value (e.g., 0.1f)
            for (i in 0 until columnCount) {
                columnInfoList.value[i].widthWeigth.value = max(columnInfoList.value[i].widthWeigth.value, 0.01f)
            }
            var sum = 0f
            columnInfoList.value.forEach {
                sum += it.widthWeigth.value
            }
            // Normalize weights to ensure they sum to 1
            columnInfoList.value.forEach {
                it.widthWeigth.value /= sum
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
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {

        columnInfoList.value.forEachIndexed { index,  columnInfo ->
            val imageVector = when(columnInfo.sortOrder.value){
                1 -> Icons.Default.KeyboardArrowUp
                -1 -> Icons.Default.KeyboardArrowDown
                else -> EmptyImageVector
            }

            Row(
                modifier = Modifier.weight(columnInfo.widthWeigth.value),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                TextButton(
                    onClick = {
                        columnInfo.sortOrder.value = when(columnInfo.sortOrder.value){
                                                        0 -> 1
                                                        1 -> -1
                                                        else -> 0
                                                    }
                        onSortOrder?.invoke( columnInfo)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Black),
                ) { Text(columnInfo.columnName,) }

                Icon(imageVector, contentDescription = "Sorted Order", modifier = Modifier.width(16.dp),)
                SearchMenu(columnInfo.columnName, onFilter)
            }

            if ( index < columnCount - 1) {
                VerticalDivider(
                    modifier = Modifier.height(40.dp)
                                .draggable(
                                    orientation = Orientation.Horizontal,
                                    state = draggableStates[index],
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
    enablePagingGrid:MutableState<Boolean>,
    onRefresh:(()->Unit)? = null,
) {
    val coroutineScope = rememberCoroutineScope()

    Row (
        modifier = then(modifier).fillMaxWidth()
            .border(BorderStroke(width = 1.dp, color = Color.LightGray))
            .background(color = MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ){

        IconButton(
            modifier = Modifier,
            enabled =  lazyListState.firstVisibleItemIndex != 0,
            onClick = { coroutineScope.launch { lazyListState.animateScrollToItem(0)  }  }
        ) {  Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Goto First Page") }

        Text ( "Count : $dataCnt" )

        IconButton(
            modifier = Modifier,
            enabled = lazyListState.canScrollForward,
            onClick = {  coroutineScope.launch { lazyListState.animateScrollToItem(dataCnt-1) } }
        ) { Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Goto Last Page") }

        IconButton(
            onClick = { coroutineScope.launch { onRefresh?.invoke() } }
        ) {  Icon(Icons.Default.Refresh, contentDescription = "Refresh")  }



        Text(
            "PagingGrid:",
        )
        Checkbox(
            checked = enablePagingGrid.value,
            onCheckedChange = { enablePagingGrid.value = it }
        )

    }
}

@Composable
fun ComposeDataGridFooter(
    currentPage: MutableState<Int> = mutableStateOf(1) ,
    pageSize: MutableState<Int>,
    dataCount:Int,
    onPageChange:((Int, Int)->Unit)?=null
) {

    val lastPage =  remember {  mutableStateOf(  if( dataCount <= pageSize.value ) 1 else { if( dataCount % pageSize.value == 0 ){ dataCount/pageSize.value } else { (dataCount/pageSize.value) + 1 } } )}

    val startRowIndex = remember { mutableStateOf( (currentPage.value-1)*pageSize.value) }

    val endRowIndex = remember { mutableStateOf(  if( currentPage.value == lastPage.value){ dataCount } else{ (pageSize.value * currentPage.value) } )}

    LaunchedEffect(key1 = currentPage.value, key2 = pageSize.value, key3 = dataCount){

        lastPage.value = if( dataCount <= pageSize.value ) 1 else { if( dataCount % pageSize.value == 0 ){ dataCount/pageSize.value } else { (dataCount/pageSize.value) + 1 } }
        startRowIndex.value = (currentPage.value-1)*pageSize.value
        endRowIndex.value =  if(currentPage.value == lastPage.value){ dataCount } else{ (pageSize.value * currentPage.value)  }

        onPageChange?.let {
            it(startRowIndex.value, endRowIndex.value)
        }

    }

    var expanded by remember { mutableStateOf(false) }

    val onChangePageSize:(Int)->Unit = {
        pageSize.value = it
        currentPage.value = 1
        expanded = false
    }

    Box(
        modifier = Modifier.fillMaxWidth().height(60.dp).border(
            BorderStroke(width = 1.dp, color = Color.LightGray),
            RoundedCornerShape(6.dp)
        ),
        contentAlignment = Alignment.Center
    ){

        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {

            Text( "Page Size:", modifier = Modifier.padding(horizontal = 20.dp) )

            Box( modifier = Modifier.height(50.dp).width(110.dp), contentAlignment = Alignment.Center, ){

                var selectedOptionText by remember { mutableStateOf("20") }

                OutlinedTextField(
                    modifier = Modifier.padding(horizontal = 0.dp),
                    value = selectedOptionText,
                    readOnly = true,
                    onValueChange = { selectedOptionText = it },
                    trailingIcon = {
                        IconButton( onClick = { expanded = !expanded}, )
                        { Icon(Icons.Default.ArrowDropDown, contentDescription = "Page Size",) }
                    },
                    singleLine = true,
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.width(100.dp),
                ) {

                    DropdownMenuItem(
                        text = { Text("20") },
                        onClick = {
                            selectedOptionText = "20"
                            onChangePageSize(selectedOptionText.toInt())
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("100") },
                        onClick = {
                            selectedOptionText = "100"
                            onChangePageSize(selectedOptionText.toInt())
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("1000") },
                        onClick = {
                            selectedOptionText = "1000"
                            onChangePageSize(selectedOptionText.toInt())
                        }
                    )





                }

            }

            Text( "${ if(dataCount == 0){0} else{  ( startRowIndex.value + 1 ) } }  to  ${ endRowIndex.value }  of  ${dataCount}" , modifier = Modifier.padding(horizontal = 20.dp))

            IconButton(
                enabled = currentPage.value > 1,
                onClick = { currentPage.value = currentPage.value - 1}
            ) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Prev Page")
            }

            Text( "Page ${currentPage.value} of ${ lastPage.value }" , modifier = Modifier.padding(horizontal = 0.dp))

            IconButton(
                enabled = currentPage.value < lastPage.value  ,
                onClick = { currentPage.value = currentPage.value + 1  }
            ) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next Page")
            }

        }
    }
}






@Composable
fun SearchMenu(columnName:String, onFilter: ((String, String, String)-> Unit)? = null) {

    var expanded by remember { mutableStateOf(false) }
    val filterText = remember { mutableStateOf("") }
    val operatorText = remember { mutableStateOf(OperatorMenu.Operators.first().toString()) }
    var isFocused by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scrollState = remember { ScrollState(0) }
    var expandedOperator by remember { mutableStateOf(false) }

    val onSearch: () -> Unit = {
        onFilter?.invoke(columnName, filterText.value, operatorText.value)
        expanded = false
        filterText.value = ""
        operatorText.value = OperatorMenu.Operators.first().toString()
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
            },
            modifier = Modifier.width(200.dp),
        ) {

            Column {

                Box( contentAlignment = Alignment.Center,){

                    OutlinedTextField(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        value = operatorText.value,
                        readOnly = true,
                        onValueChange = { operatorText.value = it },
                        label = { Text("Operator...")  },
                        trailingIcon = {
                            IconButton( onClick = { expandedOperator = !expandedOperator}, )
                            { Icon(Icons.Default.ArrowDropDown, contentDescription = "Operator",) }
                        },
                        singleLine = true,
                    )

                    DropdownMenu(
                        expanded = expandedOperator,
                        onDismissRequest = { expandedOperator = false },
                        scrollState = scrollState,
                        modifier = Modifier.width(200.dp).height(160.dp).padding(horizontal = 8.dp),
                    ) {
                        OperatorMenu.Operators.forEach { operator ->
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text(operator.toString()) },
                                onClick = {
                                    operatorText.value = operator.toString()
                                    expandedOperator = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    modifier = Modifier.padding(horizontal = 8.dp).onKeyEvent { event ->
                        if (event.key.equals(Key.Enter) && event.type.equals(KeyEventType.KeyUp) ) {
                            onSearch()
                            true
                        }else{
                            false
                        }
                    }.onFocusChanged { focusState ->  isFocused = focusState.isFocused  },
                    value = filterText.value,
                    onValueChange = { filterText.value = it  },
                    label = { Text("Search...")  },
                    trailingIcon = {
                        IconButton(
                            onClick = { onSearch()  },
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
}

object OperatorMenu {
    enum class Operator {
        Contains{ override fun toString() = "Contains"},
        DoseNotContains{ override fun toString() = "Dose Not Contains"},
        Equals{ override fun toString() = "Equals"},
        DoseNotEquals{ override fun toString() = "Dose Not Equals"},
        BeginsWith{ override fun toString() = "Begins With"},
        EndsWith{ override fun toString() = "Ends With"},
        Blank{ override fun toString() = "Blank"},
        NotBlank{ override fun toString() = "Not Blank"},
        Null{override fun toString() = "Null"},
        NotNull{override fun toString() = "Not Null"}
    }
    val Operators = listOf(
        Operator.Contains, Operator.DoseNotContains, Operator.Equals, Operator.DoseNotEquals,
        Operator.BeginsWith, Operator.EndsWith, Operator.Blank, Operator.NotBlank, Operator.Null, Operator.NotNull
    )
}


data class ColumnInfo(
    val columnName:String,
    val columnIndex:Int,
    val columnType: String,
    var sortOrder: MutableState<Int>,
    val widthWeigth: MutableState<Float>,
    val isContainNull:Boolean = false
)


val EmptyImageVector: ImageVector = ImageVector.Builder(
    name = "Empty",
    defaultWidth = 0.dp,
    defaultHeight = 0.dp,
    viewportWidth = 0f,
    viewportHeight = 0f
).build()