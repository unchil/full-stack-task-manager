package org.example.ktor

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.h1
import kotlinx.html.id
import kotlinx.html.style
import kotlin.coroutines.cancellation.CancellationException

fun main() {
    window.onload = {
        createLayOut{
            try {
                createContent(ContainerDiv.ID.MofSeaQualityArea)
                createContent(ContainerDiv.ID.BoxPlot)
                createContent(ContainerDiv.ID.LayerBars)
                createContent(ContainerDiv.ID.SeaArea)
                createContent(ContainerDiv.ID.RibbonArea)
                createContent(ContainerDiv.ID.AgGridCurrent)
            } catch (e: CancellationException) {
                // CancellationException은 코루틴 취소 시 발생하는 특별한 예외이므로,
                // 일반적인 에러와 분리하여 처리하는 것이 좋습니다.
                println("An error occurred: ${e.message}")
            } catch (e: Exception) {
                println("An error occurred: ${e.message}")
            }

        }
    }
}


fun createLayOut( completeHandle:()->Unit) {
    val body = document.body ?: error("No body")
    body.append {

        h1 { +"Korea Sea Water Quality Information"; style="text-align:center;" }

        div { id = ContainerDiv.ID.MofSeaQualityArea.name}
        div { id = ContainerDiv.ID.MofSeaQuality.name}

        div { id = ContainerDiv.ID.BoxPlot.name}

        div { id = ContainerDiv.ID.LayerBars.name}

        div { id = ContainerDiv.ID.SeaArea.name}
        div { id = ContainerDiv.ID.Line.name}

        div { id = ContainerDiv.ID.RibbonArea.name}
        div { id = ContainerDiv.ID.Ribbon.name}

        div {
            id = ContainerDiv.ID.AgGridCurrent.name
            style="width: 1360px;height: 600px"
        }

    }
    completeHandle()
}