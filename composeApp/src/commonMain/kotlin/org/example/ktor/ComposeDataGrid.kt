package org.example.ktor

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.then
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.ktor.theme.AppTheme
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun ComposeDataGrid(
    modifier:Modifier = Modifier,
    columnNames:List<String>,
    data:List<List<Any?>>,
    reloadData :()->Unit){

    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val columnInfo = remember { mutableStateOf(makeColInfo(columnNames, data) ) }
    val enablePagingGrid = remember { mutableStateOf(false) }

// No Remember
    var presentData by mutableStateOf<List<Any?>>(data)
    var pagingData by  mutableStateOf<List<Any?>>(data)
//

    var sortedIndexList = remember { mutableListOf<Int>() }
    var startRowNum by remember {  mutableStateOf(0)}
    val currentPage = remember {  mutableStateOf(1)}
    val pageSize = remember {  mutableStateOf(20)}

    val enableDarkMode = remember { mutableStateOf(false) }

    val initSortOrder:()->Unit = {
        sortedIndexList.clear()
        columnInfo.value.forEach { it.sortOrder.value = 0 }
    }

    val initPageData:()->Unit = {
        currentPage.value = 1

        val lastPage = if( presentData.size <= pageSize.value ){
            1
        } else {
            if( presentData.size % pageSize.value == 0 ){
                presentData.size/pageSize.value
            } else {
                (presentData.size/pageSize.value) + 1
            }
        }

        val endIndex = if( currentPage.value == lastPage){
            presentData.size
        } else{
            pageSize.value * currentPage.value
        }

        val currentPageData = mutableListOf<List<Any?>>()

        for ( i in 0  until endIndex){
            currentPageData.add( presentData[i] as List<Any?>)
        }

        pagingData = currentPageData

        coroutineScope.launch {
            lazyListState.animateScrollToItem(0)
        }
    }

    val updateOrginalColumnIndex:(MutableState<List<ColumnInfo>>) -> Unit = {
        newColumnInfoList ->
        val tempSortedIndexList =  mutableListOf<Int>()
        newColumnInfoList.value.forEach {
            if(sortedIndexList.contains(it.originalColumnIndex)){
                tempSortedIndexList.add(it.columnIndex)
            }
            it.originalColumnIndex = it.columnIndex
        }
        sortedIndexList = tempSortedIndexList
    }

    val updateDataColumnOrder:(MutableState<List<ColumnInfo>>) -> Unit = {
        newColumnInfoList ->
        val newData = presentData.map { row ->
            val oldRow = row as List<Any?>
            val newRow = mutableListOf<Any?>().apply { repeat(oldRow.size) { add(null) } }

            newColumnInfoList.value.forEach { colInfo ->
                newRow[colInfo.columnIndex] = oldRow[colInfo.originalColumnIndex]
            }
            newRow
        }

        presentData = newData
        updateOrginalColumnIndex(newColumnInfoList)
        if(enablePagingGrid.value) {
            initPageData()
        }
    }

    val updateSortedIndexList:(colInfo:ColumnInfo)->Unit = {
        if(sortedIndexList.isEmpty() ){
            sortedIndexList.add(it.columnIndex)
        } else {
            if (it.sortOrder.value == 0){
                sortedIndexList.remove(it.columnIndex)

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

    val onMultiSortedOrder:(colInfo:ColumnInfo)->Unit = {
        colInfo ->

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

           val data:List<List<Any?>> = presentData.filterIsInstance<List<Any?>>()
            presentData = data.sortedWith(comparator)
        }

        if(enablePagingGrid.value) {
            initPageData()
        }
    }
    
    val onFilter:(columnName:String, searchText:String, operator:String) -> Unit = { columnName, searchText, operator  ->

        columnInfo.value.find { it.columnName == columnName }?.let {columInfo ->
            presentData = when(operator){
                OperatorMenu.Operator.Contains.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex].toString().contains(searchText)
                    }
                OperatorMenu.Operator.DoseNotContains.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex].toString().contains(searchText).not()
                    }
                OperatorMenu.Operator.Equals.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex].toString().equals(searchText)
                    }
                OperatorMenu.Operator.DoseNotEquals.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex].toString().equals(searchText).not()
                    }
                OperatorMenu.Operator.BeginsWith.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex].toString().startsWith(searchText)
                    }
                OperatorMenu.Operator.EndsWith.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex].toString().endsWith(searchText)
                    }
                OperatorMenu.Operator.Blank.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex].toString().isBlank()
                    }
                OperatorMenu.Operator.NotBlank.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex].toString().isNotBlank()
                    }
                OperatorMenu.Operator.Null.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex] == null
                    }
                OperatorMenu.Operator.NotNull.toString() ->
                    presentData.filter {
                        it as List<*>
                        it[columInfo.columnIndex] != null
                    }
                else -> {
                    presentData
                }
            }


        }



        if(enablePagingGrid.value) {
            initPageData()
        }
    }

    val onRefresh:()-> Unit = {
        reloadData()
        presentData = data
        currentPage.value = 1
        columnInfo.value =  makeColInfo(columnNames, data)
        initSortOrder()
        if(enablePagingGrid.value) {
            initPageData()
        }
        coroutineScope.launch {
            lazyListState.animateScrollToItem(0)
        }
    }

    val onPageChange:(Int, Int)->Unit = {
        startIndex, endIndex->
        startRowNum = startIndex
        val currentPageData = mutableListOf<List<Any?>>()
        for ( i in startIndex  until endIndex){
            currentPageData.add( presentData[i] as List<Any?>)
        }
        pagingData = currentPageData.toList()
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

    AppTheme(enableDarkMode = enableDarkMode.value) {



            Scaffold(
                modifier = then(modifier)
                    .fillMaxSize()
                    .border(
                        BorderStroke(width = 1.dp, color = Color.Black),
                        RoundedCornerShape(2.dp)
                    ),
                topBar = {
                    ComposeDataGridHeader(
                        modifier = Modifier.fillMaxWidth(),
                        columnInfo = columnInfo,
                        onSortOrder = onMultiSortedOrder,
                        onFilter = onFilter,
                        updateDataColumnOrder = updateDataColumnOrder,
                    )
                },
                bottomBar = {
                    if (enablePagingGrid.value) {
                        ComposeDataGridFooter(
                            currentPage,
                            pageSize,
                            presentData.size,
                            onPageChange,
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = contentColorFor(MaterialTheme.colorScheme.surface),
            ) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        state = lazyListState,
                        contentPadding = PaddingValues(1.dp),
                        userScrollEnabled = true,
                    ) {

                        items(
                            count = if (enablePagingGrid.value) {
                                pagingData.size
                            } else {
                                presentData.size
                            }
                        ) { index ->

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceContainerLowest )
                                    .border(
                                        BorderStroke(
                                            width = 1.dp,
                                            color = Color.LightGray.copy(alpha = 0.2f)
                                        )
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {

                                // row number
                                Text(
                                    text = if (enablePagingGrid.value) {
                                        (startRowNum + index + 1).toString()
                                    } else {
                                        (index + 1).toString()
                                    },
                                    modifier = Modifier.width(40.dp),
                                    textAlign = TextAlign.Center
                                )

                                ComposeDataGridRow(
                                    columnInfo.value,
                                    data = if (enablePagingGrid.value) {
                                        pagingData[index] as List<Any?>
                                    } else {
                                        presentData[index] as List<Any?>
                                    }
                                )
                            }

                        }
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        ComposeDataGridFooter(
                            modifier = Modifier
                                .width(500.dp)
                                .padding(
                                    bottom = if (enablePagingGrid.value) {
                                        90.dp
                                    } else {
                                        40.dp
                                    }
                                ),
                            lazyListState = lazyListState,
                            dataCnt = if (enablePagingGrid.value) {
                                pagingData.size
                            } else {
                                presentData.size
                            },
                            enablePagingGrid = enablePagingGrid,
                            enableDarkMode = enableDarkMode,
                            onRefresh = onRefresh
                        )
                    }

                }
            }


    }


}

@Composable
fun ComposeDataGridRow( columnInfo:List< ColumnInfo>, data:List<Any?>) {
    Row (
        modifier = Modifier.fillMaxWidth().height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        columnInfo.forEachIndexed {
            index, columnInfo ->
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
    updateDataColumnOrder: (MutableState<List<ColumnInfo>>) -> Unit , ) {

    Row (
        modifier =  then(modifier)
            .fillMaxWidth()
            .height(46.dp)
            .border(
                border = BorderStroke(width = 1.dp, color =  MaterialTheme.colorScheme.onSecondaryContainer),
                shape = RoundedCornerShape(2.dp) )
            .background(MaterialTheme.colorScheme.secondaryContainer),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ){
        // row number space
        Text("", Modifier.width( 40.dp))

        ComposeColumnRow(
            columnInfoList = columnInfo,
            updateColumnInfo = updateDataColumnOrder,
            onSortOrder = onSortOrder,
            onFilter = onFilter, )
    }

}

@Composable
fun ComposeColumnRow(
    columnInfoList: MutableState<List< ColumnInfo>>,
    updateColumnInfo: ((MutableState<List<ColumnInfo>>) -> Unit)? = null,
    onSortOrder:(( ColumnInfo) -> Unit)? = null,
    onFilter:((String, String, String) -> Unit)? = null, ){

    require(columnInfoList.value.size >= 2) { "column must be at least 2" }

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current.density
    var rowWidthInDp by remember { mutableStateOf(0.dp) }
    val dividerPositions = remember { MutableList(columnInfoList.value.size) { 0.dp } }
    val offsetList = remember {  MutableList(columnInfoList.value.size ) { mutableStateOf(IntOffset.Zero) } }
    val boxSizePx = remember {  MutableList(columnInfoList.value.size ){ mutableStateOf(IntSize.Zero) } }
    val interactionSourceList = remember { MutableList(columnInfoList.value.size ){ MutableInteractionSource() } }
    val currentHoverEnterInteraction = remember { MutableList(columnInfoList.value.size ){
        mutableStateOf<HoverInteraction.Enter?>(null) }
    }

    val dividerThickness = 1.dp
    val totalWidth = rowWidthInDp - (dividerThickness * (columnInfoList.value.size - 1))
    val draggableStates = (0 until columnInfoList.value.size - 1).map {
        index ->
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
            columnInfoList.value[index].widthWeigth.value =
                (newWeightBefore / oldSumBefore) * columnInfoList.value[index].widthWeigth.value
            // After
            for (i in index + 1 until columnInfoList.value.size) {
                columnInfoList.value[i].widthWeigth.value =
                    (newWeightAfter / oldSumAfter) *columnInfoList.value[i].widthWeigth.value
            }
            // Ensure weights don't go below a minimum value (e.g., 0.1f)
            for (i in 0 until columnInfoList.value.size) {
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
            val initialPosition = (rowWidthInDp / columnInfoList.value.size)
            for (i in 0 until columnInfoList.value.size ) {
                dividerPositions[i] = initialPosition * (i + 1) - (dividerThickness * (i + 1) / 2)
            }
        }
    }

    interactionSourceList.forEachIndexed { index, interactionSource ->
        LaunchedEffect(interactionSource){
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is HoverInteraction.Enter -> {
                        currentHoverEnterInteraction[index].value = interaction
                    }
                    is HoverInteraction.Exit -> {
                        currentHoverEnterInteraction[index].value = null
                    }
                    else -> {}
                }
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
            val draggedItemAlpha = remember { mutableStateOf(1f) }
            val animatedAlpha by animateFloatAsState(
                targetValue = if (offsetList[index].value == IntOffset.Zero) 1f else  draggedItemAlpha.value,
                label = "alphaAnimation"
            )
            val onDragStart: (Offset) -> Unit = {
                draggedItemAlpha.value = 0.5f
            }
            val onDragEnd:() -> Unit = {
                currentHoverEnterInteraction[index].value?.let {
                    coroutineScope.launch {
                        interactionSourceList[index].emit(HoverInteraction.Exit(it))
                    }
                }

                var appendBoxSize = 0
                for ( i in 0 until index ) {
                    appendBoxSize += boxSizePx[i].value.width
                }
                val currentDp = (( offsetList[index].value.x + boxSizePx[index].value.width / 2 + appendBoxSize ) / density).dp


                val targetColumnIndex = findIndexFromDividerPositions(currentDp, dividerPositions, index, density)
                val currentList = columnInfoList.value.toMutableList()
                val draggedColumn = currentList.removeAt(index)
                currentList.add(targetColumnIndex, draggedColumn)

                currentList.forEachIndexed{ newIndex, colInfo ->
                    colInfo.columnIndex = newIndex
                }

                columnInfoList.value = currentList.toList()

                updateColumnInfo?.let{
                    it(columnInfoList)
                }

                offsetList[index].value = IntOffset.Zero
                draggedItemAlpha.value = 1f
            }
            val onDragCancel: () -> Unit = {
                offsetList[index].value = IntOffset.Zero
                draggedItemAlpha.value = 1f
            }
            val onDrag: (PointerInputChange, Offset) -> Unit = { pointerInputChange, offset ->
                pointerInputChange.consume()
                val offsetChange = IntOffset(offset.x.roundToInt(), offset.y.roundToInt())
                offsetList[index].value = offsetList[index].value.plus(offsetChange)
            }
            val onClick: () -> Unit = {
                columnInfo.sortOrder.value = when(columnInfo.sortOrder.value){
                    0 -> 1
                    1 -> -1
                    else -> 0
                }
                onSortOrder?.invoke( columnInfo)
            }

            Row(
                modifier = Modifier
                    .weight(columnInfo.widthWeigth.value)
                    // onGloballyPositioned를 사용하여 Box의 크기를 가져옴
                    .onGloballyPositioned { layoutCoordinates ->
                        boxSizePx[index].value = layoutCoordinates.size
                    }
                    .pointerInput(Unit) {
                        detectDragGestures (
                            onDragStart = onDragStart,
                            onDragEnd = onDragEnd  ,
                            onDragCancel = onDragCancel,
                            onDrag = onDrag)
                    }
                    .offset { offsetList[index].value }
                    .alpha(animatedAlpha),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                TextButton(
                    onClick = onClick,
                    interactionSource = interactionSourceList[index],
                ) {
                    Text(columnInfo.columnName, color = MaterialTheme.colorScheme.onSurface)
                }

                Icon(
                    imageVector,
                    contentDescription = "Sorted Order",
                    modifier = Modifier.width(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )

                SearchMenu(
                    columnInfo.columnName,
                    onFilter
                )
            }


            if ( index < columnInfoList.value.size - 1) {
                VerticalDivider(
                    modifier = Modifier
                        .height(40.dp)
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = draggableStates[index],
                        ),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
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
    enableDarkMode:MutableState<Boolean>,
    onRefresh:(()->Unit)? = null, ) {

    val coroutineScope = rememberCoroutineScope()

    Row (
        modifier = then(modifier)
            .fillMaxWidth().height(46.dp)
            .border( BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onTertiaryContainer), shape = RoundedCornerShape(2.dp))
            .background(color  =MaterialTheme.colorScheme.tertiaryContainer),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
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



        Checkbox(
            modifier = Modifier.scale(0.8f),
            checked = enablePagingGrid.value,
            onCheckedChange = { enablePagingGrid.value = it }
        )

        Text( "Pagination")

        Checkbox(
            modifier = Modifier.scale(0.8f),
            checked = enableDarkMode.value,
            onCheckedChange = { enableDarkMode.value = it }
        )
        Text( "DarkMode")

    }
}

@Composable
fun ComposeDataGridFooter(
    currentPage: MutableState<Int> = mutableStateOf(1) ,
    pageSize: MutableState<Int>,
    dataCount:Int,
    onPageChange:((Int, Int)->Unit)?=null
) {

    var expanded by remember { mutableStateOf(false) }

    val lastPage =  remember { mutableStateOf(
        value = if( dataCount <= pageSize.value ) {
            1
        } else {
            if( dataCount % pageSize.value == 0 ){
                dataCount/pageSize.value
            } else {
                (dataCount/pageSize.value) + 1
            }
        }
    )}

    val startRowIndex = remember { mutableStateOf( (currentPage.value-1) * pageSize.value) }

    val endRowIndex = remember { mutableStateOf(
        value = if( currentPage.value == lastPage.value){
            dataCount
        } else{
            (pageSize.value * currentPage.value)
        }
    )}

    val onChangePageSize:(Int)->Unit = {
        pageSize.value = it
        currentPage.value = 1
        expanded = false
    }

    LaunchedEffect(key1 = currentPage.value, key2 = pageSize.value){
        lastPage.value = if( dataCount <= pageSize.value ){
            1
        }else {
            if( dataCount % pageSize.value == 0 ){
                dataCount/pageSize.value
            } else {
                (dataCount/pageSize.value) + 1
            }
        }
        startRowIndex.value = (currentPage.value-1)*pageSize.value
        endRowIndex.value =  if(currentPage.value == lastPage.value){
            dataCount
        } else{
            pageSize.value * currentPage.value
        }

        onPageChange?.let {
            it(startRowIndex.value, endRowIndex.value)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(color  =MaterialTheme.colorScheme.secondaryContainer)
            .border( BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSecondaryContainer),
            RoundedCornerShape(2.dp) ),
        contentAlignment = Alignment.Center
    ){

        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {

            Text(
                "Page Size:",
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Box(
                modifier = Modifier
                    .width(110.dp).scale(0.8f),
                contentAlignment = Alignment.Center,
            ){

                var selectedOptionText by remember { mutableStateOf("20") }
                val pageSizes = listOf("20", "100", "1000")

                OutlinedTextField(
                    modifier = Modifier.padding(horizontal = 0.dp),
                    value = selectedOptionText,
                    readOnly = true,
                    onValueChange = { selectedOptionText = it },
                    trailingIcon = {
                        IconButton( onClick = { expanded = !expanded}, ){
                            Icon(Icons.Default.ArrowDropDown,
                                contentDescription = "Page Size", )
                        }
                    },
                    singleLine = true,
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.width(100.dp).background(color  =MaterialTheme.colorScheme.surface),
                ) {
                    pageSizes.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedOptionText = option
                                onChangePageSize(selectedOptionText.toInt())
                            }
                        )
                    }
                }

            }

            Text(
                text = "${ if(dataCount == 0){
                    0
                } else{  
                    ( startRowIndex.value + 1 ) 
                }}  to  ${ endRowIndex.value } of  ${dataCount}" ,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            IconButton(
                enabled = currentPage.value > 1,
                onClick = { currentPage.value = currentPage.value - 1}
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Prev Page")
            }

            Text(
                text = "Page ${currentPage.value} of ${ lastPage.value }" ,
                modifier = Modifier.padding(horizontal = 0.dp)
            )

            IconButton(
                enabled = currentPage.value < lastPage.value  ,
                onClick = { currentPage.value = currentPage.value + 1  }
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Page")
            }

        }
    }
}


@Composable
fun SearchMenu(
    columnName:String,
    onFilter: ((String, String, String)-> Unit)? = null ) {

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

    Box(
        contentAlignment = Alignment.Center,
    ){

        IconButton( onClick = {  expanded = !expanded } ) {
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Filter")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                filterText.value = ""
            },
            modifier = Modifier.width(180.dp).background(color =MaterialTheme.colorScheme.tertiaryContainer),
        ) {

            Column() {

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
                        modifier = Modifier.width(200.dp).height(160.dp).background(color  =MaterialTheme.colorScheme.tertiaryContainer),
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



val makeColInfo: (columnNames: List<String>, data: List<List<Any?>>) -> List<ColumnInfo> = {
        columnNames, data ->

    val isContainNull = columnNames.map { false }.toMutableList()

    data.forEach{
        it.forEachIndexed {  index, any ->
            if(!isContainNull[index].equals(null)){
                isContainNull[index] = (any == null)
            }
        }
    }

    val columnInfo = mutableListOf< ColumnInfo>()

    columnNames.forEachIndexed{ columnIndex, columnName ->

        val columnType = data.first {
            it[columnIndex] != null
        }[columnIndex]?.let {
            it::class.simpleName.toString()
        } ?: "NULL"

        columnInfo.add(
            ColumnInfo(
                columnName,
                columnIndex,
                columnIndex,
                columnType,
                mutableStateOf(0),
                mutableStateOf(1f / columnNames.size),
                isContainNull[columnIndex]
            )
        )
    }
    columnInfo
}

val  findIndexFromDividerPositions: (
    currentDp:Dp,
    dividerPositions: MutableList<Dp>,
    index: Int,
    density: Float ) -> Int = { currentDp, dividerPositions, index, density ->

    val oldDp = dividerPositions[index]

    var result:Int = index

    when(currentDp){
        in 0.dp.. dividerPositions[0] -> {
            result = 0
        }
        in dividerPositions.last()..Int.MAX_VALUE.dp -> {
            result = dividerPositions.size - 1
        }
        in (oldDp + 1.dp)..currentDp -> {
            for ( i in index + 1 until dividerPositions.size ) {
                if ( currentDp <= dividerPositions[i]) {
                    result = i
                    break
                }
            }
        }
        in currentDp .. (oldDp - 1.dp) -> {
            for ( i in (0 until index ).reversed() ) {
                if ( currentDp >= dividerPositions[i]) {
                    result = i + 1
                    break
                }
            }
        }
        else -> {
            result = index
        }
    }
    result
}

data class ColumnInfo(
    val columnName:String,
    var columnIndex:Int,          // 현재 컬럼의 index
    var originalColumnIndex: Int, // drag 이전 컬럼 index
    val columnType: String,
    var sortOrder: MutableState<Int>,
    val widthWeigth: MutableState<Float>,
    val isContainNull:Boolean
)

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

val EmptyImageVector: ImageVector = ImageVector.Builder(
    name = "Empty",
    defaultWidth = 0.dp,
    defaultHeight = 0.dp,
    viewportWidth = 0f,
    viewportHeight = 0f
).build()