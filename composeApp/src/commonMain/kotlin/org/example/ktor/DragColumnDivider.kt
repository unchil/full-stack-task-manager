package org.example.ktor

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun DragColumnDivider(columnInfo: List<String>) {

    require(columnInfo.size >= 2) { "column must be at least 2" }


    val weights = remember { MutableList(columnInfo.size) { mutableStateOf( 1f / columnInfo.size.toFloat() )} }

    val dividerPositions = remember { MutableList(columnInfo.size - 1) { 0.dp } }
    val density = LocalDensity.current.density
    var rowWidthInDp by remember { mutableStateOf(0.dp) }
    val dividerThickness = 1.dp
    val interactionSources = remember { List(columnInfo.size - 1) { MutableInteractionSource() } }

    val isDraggedStates = interactionSources.map { it.collectIsDraggedAsState() }

    val totalWidth = rowWidthInDp - (dividerThickness * (columnInfo.size - 1))

    val draggableStates = (0 until columnInfo.size - 1).map { index ->

        rememberDraggableState { delta ->

            val newPositionDp = ( dividerPositions[index] + (delta/density).dp  ).coerceIn(0.dp, totalWidth)

            dividerPositions[index] = newPositionDp

            val newWeightBefore = (newPositionDp / totalWidth)
            val newWeightAfter = 1f - newWeightBefore

            var oldSumBefore = 0f
            for (i in 0 until index + 1){
                oldSumBefore += weights[i].value
            }

            val oldSumAfter = 1f - oldSumBefore

            // Before
            for (i in 0 until index) {
                weights[i].value =  weights[i].value
            }

            // Standard
            weights[index].value = (newWeightBefore / oldSumBefore) * weights[index].value

            // After
            for (i in index + 1 until columnInfo.size) {
                weights[i].value = (newWeightAfter / oldSumAfter) * weights[i].value
            }

            // Ensure weights don't go below a minimum value (e.g., 0.1f)
            for (i in 0 until columnInfo.size) {
                weights[i].value = max(weights[i].value, 0.01f)
            }

            var sum = 0f
            weights.forEach {
                sum += it.value
            }

            // Normalize weights to ensure they sum to 1
            for (i in 0 until columnInfo.size) {
                weights[i].value /= sum
            }

        }

    }

    LaunchedEffect(rowWidthInDp) {
        if (rowWidthInDp > 0.dp) {
            val initialPosition = (rowWidthInDp / columnInfo.size)
            for (i in 0 until columnInfo.size - 1) {
                dividerPositions[i] = initialPosition * (i + 1) - (dividerThickness * (i + 1) / 2)
            }
        }
    }



    Row(
        Modifier
            .fillMaxWidth()
            .onGloballyPositioned { layoutResult ->
                rowWidthInDp = (layoutResult.size.width / density).dp
            }) {

        for (i in 0 until columnInfo.size) {
            Box(
                Modifier
                    .weight(weights[i].value)
                    .fillMaxHeight()
                    .background(
                        when (i % 3) {
                            0 -> Color.LightGray
                            1 -> Color.DarkGray
                            else -> Color.Gray
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(columnInfo[i],
                    color = if (i % 3 == 1) Color.White else Color.Black
                )
            }

            if (i < columnInfo.size - 1) {
                Divider(
                    Modifier
                        .width(dividerThickness)
                        .fillMaxHeight()
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = draggableStates[i],
                            interactionSource = interactionSources[i]
                        ),
                    color = Color.LightGray,
                    thickness = dividerThickness
                )
            }
        }
    }
}


