package org.example.ktor

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.h1
import kotlinx.html.id
import kotlinx.html.style

fun main() {
    window.onload = {
        createLayOut{

            createContent(ContainerDiv.ID.BoxPlot)

            createContent(ContainerDiv.ID.LayerBars)

            createContent(ContainerDiv.ID.SeaArea)

            createContent(ContainerDiv.ID.RibbonArea)

            createContent(ContainerDiv.ID.AgGridCurrent)

        }
    }
}


fun createLayOut( completeHandle:()->Unit) {
    val body = document.body ?: error("No body")
    body.append {

        h1 { +"Nifs Sea Water Temperature Infomation"; style="text-align:center;" }

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