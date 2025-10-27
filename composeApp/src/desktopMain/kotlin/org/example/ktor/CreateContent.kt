package org.example.ktor

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.byUnicodePattern
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
import org.jetbrains.letsPlot.scale.scaleXContinuous
import org.jetbrains.letsPlot.scale.scaleXDateTime
import org.jetbrains.letsPlot.scale.scaleXDiscrete
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
    val yMin: Float? = data.getValue("Temperature").minOfOrNull {
        it as Float
    }
    val yMax: Float? = data.getValue("Temperature").maxOfOrNull {
        it as Float
    }
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
            scaleYContinuous(
                limits =  (yMin?.minus(0.5) ?: 0.0) to (yMax?.plus(0.5) ?: 0.0),
                breaks = ( (yMin?.toInt()?:0).. (yMax?.toInt()?:1) ).toList(),
                format = ".1f"
            ) +
            labs( title="실시간 수온 정보", y="수온 °C", x="관측지점", fill="관측수심", caption="Nifs") +
            theme

}

fun createBoxPlotChart(data: Map<String,List<Any>>): Plot {
    return letsPlot(
        data
    )  +
            scaleColorViridis(option = "C", end = 0.8) +
            geomBoxplot{
             //   x = asDiscrete("ObservatoryName", orderBy = "..middle..", order = 1)
                x = asDiscrete("ObservatoryName", order = 1)
             //   x = "ObservatoryName"
                y = "Temperature"
                color = "..middle.."
            } +
            labs(title="수온 일일 통계 정보", y="수온 °C", x="관측지점", color="수온 °C", caption="Nifs") +
            theme
}

fun createLineChart( data: Map<String,List<Any>> , entrie:String?): Plot {
    return letsPlot(data) +
            geomLine { x="CollectingTime"; y="Temperature"; color="ObservatoryName"} +
            labs( title="Korea ${entrie?:""} Sea Water Temperature Line", y="수온 °C", x="관측시간", color="관측지점", caption="Nifs") +
            theme
}

fun createLineChart2( data: Map<String,List<Any>> , entrie:String?): Plot {

    return letsPlot(data) +
            geomLine { x="CollectingTime"; y="Value"; color="ObservatoryName"} +
            scaleXDateTime(
                format = "%d %H" // 원하는 "dd HH" 형식 지정
            ) +
            labs( title="Korea Sea Water Information", y= entrie?:"", x="관측시간", color="관측지점", caption="Mof") +
            theme
}

fun createRibbonChart(data: Map<String,List<Any>>, entrie:String?): Plot {
    val yMin: Float? = data.getValue("TemperatureMin").minOfOrNull {
        it as Float
    }
    val yMax: Float? = data.getValue("TemperatureMax").maxOfOrNull {
        it as Float
    }

    return letsPlot(data) +
            geomRibbon(alpha = 0.1){
                x="CollectingTime"
                ymin="TemperatureMin"
                ymax="TemperatureMax"
                fill="ObservatoryName"
            } +
            geomLine( showLegend=false ) { x="CollectingTime"; y="TemperatureAvg"; color="ObservatoryName"} +
            scaleYContinuous(
                limits =  (yMin?.minus(0.5) ?: 0.0) to (yMax?.plus(0.5) ?: 0.0),
                breaks = ( (yMin?.toInt()?:0).. (yMax?.toInt()?:1) ).toList(),
                format = ".1f"
            ) +
            labs(title="Korea ${entrie?:""} Sea Water Temperature Ribbon", x="관측시간", y="수온 °C", fill="관측지점", caption="Nifs") +
            theme
}