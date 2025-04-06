package org.example.ktor

import org.jetbrains.letsPlot.asDiscrete
import org.jetbrains.letsPlot.geom.geomBar
import org.jetbrains.letsPlot.geom.geomBoxplot
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.geom.geomRibbon
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.pos.positionDodge
import org.jetbrains.letsPlot.scale.scaleColorViridis
import org.jetbrains.letsPlot.scale.scaleYContinuous
import org.jetbrains.letsPlot.themes.elementText
import org.jetbrains.letsPlot.themes.theme
import org.jetbrains.letsPlot.tooltips.layerTooltips

val theme = theme(
    plotTitle= elementText(family="AppleGothic"),
    axisTextX= elementText( family="AppleGothic", angle=45),
    axisTitle= elementText(family="AppleGothic"),
    axisTitleY= elementText(family="AppleGothic"),
    axisTitleX= elementText(family="AppleGothic" ) ,
    legendTitle= elementText(family="AppleGothic"),
    legendText= elementText(family="AppleGothic"),
    axisTooltip= elementText(family="AppleGothic"),
    axisTooltipText= elementText(family="AppleGothic"),
    tooltip= elementText(family="AppleGothic"),
    tooltipText= elementText(family="AppleGothic"),
    tooltipTitleText= elementText(family="AppleGothic") )

fun createBarChart(data: Map<String,List<Any>>): Plot {
    return letsPlot(data) {
        x = "ObservatoryName"
        weight = "Temperature" } +
            geomBar(
                position = positionDodge(),
                alpha = 0.6,
                tooltips= layerTooltips()
                    .line("수집시간|@CollectionTime")
                    .line("관측지점|@ObservatoryName/@ObservatoryCode")
                    .line("관측수심|@ObservatoryDepth")
                    .line("온도|^y °C" )
            ) {
                fill = "ObservatoryDepth" } +
            labs( title="실시간 수온 정보", y="수온 °C", x="관측지점", fill="관측수심", caption="Nifs") +
            scaleYContinuous(limits = Pair(0, 15)) +
            theme

}

fun createBoxPlotChart(data: Map<String,List<Any>>): Plot {
    val sta_nam_korByMiddle = asDiscrete("ObservatoryName", orderBy = "..middle..", order = 1)
    return letsPlot(data)  +
            scaleColorViridis(option = "C", end = 0.8) +
            geomBoxplot{
                x= sta_nam_korByMiddle
                y="Temperature"
                color = "..middle.."
            } +
            scaleYContinuous(limits = Pair(0, 15)) +
            labs(title="수온 일일 통계 정보", y="수온 °C", x="관측지점", color="수온 °C", caption="Nifs") +
            theme
}

fun createLineChart(data: Map<String,List<Any>>): Plot {
    return letsPlot(data) +
            geomLine { x="CollectingTime"; y="Temperature"; color="ObservatoryName"} +
            labs( title="Korea EastSea Water Temperature Line", y="수온 °C", x="관측시간", color="관측지점", caption="Nifs") +
            scaleYContinuous(limits=Pair(4,15) ) +
            theme
}

fun createRibbonChart(data: Map<String,List<Any>>): Plot {
    return letsPlot(data) +
            geomRibbon(alpha = 0.1){
                x="CollectingTime"
                ymin="TemperatureMin"
                ymax="TemperatureMax"
                fill="ObservatoryName"
            } +
            geomLine( showLegend=false ) { x="CollectingTime"; y="TemperatureAvg"; color="ObservatoryName"} +
            scaleYContinuous(limits=Pair(4,15) ) +
            labs(title="Korea EastSea Water Temperature Ribbon", x="관측시간", y="수온 °C", fill="관측지점", caption="Nifs") +
            theme
}