package org.example.ktor

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.byUnicodePattern
import org.example.ktor.WATER_QUALITY.name
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
import org.jetbrains.letsPlot.themes.margin
import org.jetbrains.letsPlot.themes.theme
import org.jetbrains.letsPlot.tooltips.layerTooltips

val theme = theme(
    plotTitle= elementText(family="AppleGothic", face="bold"),
    plotSubtitle = elementText(family = "AppleGothic",  size=14),
    plotCaption = elementText(family = "AppleGothic",  face="bold"),
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
    tooltipTitleText= elementText(family="AppleGothic")
)

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

fun createLineChart2( data: Map<String,List<Any>> , qualityType: WATER_QUALITY.QualityType): Plot {

    val desc:Pair<String, String> = when(qualityType){
        WATER_QUALITY.QualityType.rtmWtchWtem -> {
           Pair("양식에 적합한 해양 수질 온도는 기르는 어종에 따라 크게 다르며, " +
                   "생물의 건강한 성장과 생존을 위해 매우 중요한 요소입니다. \n" +
                   "일반적으로는 겨울철 12℃ 이상, 여름철 28℃ 이하를 유지하는 것이 좋습니다. 주요 양식 어종별 적정 수온은 \n" +
                   "넙치:21~24℃, 조피볼락(우럭):12~21℃, 뱀장어:25~26℃, 바지락:15~22℃, 전복: 15~20℃, 돔류(참돔, 감성돔, 돌돔): 저수온에 약하며 생존 가능 최적수온은 6~7℃.  ",
               "℃ (섭씨)"
           )
        }
        WATER_QUALITY.QualityType.rtmWqCndctv -> {
            Pair("해수의 전기전도도는 약 50 mS/cm (밀리시멘스/센티미터) 또는 50,000 μS/cm (마이크로지멘스/센티미터)입니다. \n" +
                    "이는 담수보다 훨씬 높으며, 해수의 염분 함량, 온도, 압력 등 여러 요인에 의해 영향을 받습니다. \n" +
                    "해수의 높은 전기전도도는 물에 녹아 있는 다량의 이온 때문이며, 전기전도도를 측정하여 해수의 염분 농도를 추정할 수 있습니다. ",
                "mS/cm (밀리시멘스 퍼 센티미터)"
            )
        }
        WATER_QUALITY.QualityType.ph -> {
            Pair("해수의 수소이온농도는 일반적으로 약알칼리성을 띠며 pH 7.9 ~ 8.1 정도입니다. \n" +
                    "대기 중 이산화탄소 증가로 해수가 이산화탄소를 흡수하면서 해수의 수소이온농도가 증가하고 pH가 낮아지는 현상인 해양 산성화가 진행 중입니다. \n" +
                    "이로 인해 해수의 pH는 점차 낮아지고 있으며, 해양 생태계에 영향을 미칩니다.",
                "pH")
        }
        WATER_QUALITY.QualityType.rtmWqDoxn -> {
            Pair("해수 용존 산소량은 표층(수심 100m 이내)에서 광합성과 대기 중 산소 용해로 인해 가장 많으며, \n" +
                    "수심이 깊어질수록 호흡 작용과 사체 분해로 감소하다가, 극지방의 찬 해수가 심층으로 내려가면서 다시 증가합니다. \n" +
                    "또한, 수온이 낮고 염분이 낮을수록, 그리고 기압이 높을수록 용존 산소량이 많아집니다",
                "mg/L"
            )
        }
        WATER_QUALITY.QualityType.rtmWqTu -> {
            Pair("해수 수질 탁도는 물에 부유한 입자 때문에 물이 얼마나 흐린지를 나타내는 지표로, 주로 빛을 산란시키는 정도를 측정합니다. \n" +
                    "이는 해양 생태계와 수질에 큰 영향을 미치며, 측정 단위는 주로 NTU (Nephelometric Turbidity Unit)를 사용합니다. \n" +
                    "해수의 탁도를 높이는 요인으로는 다양한 부유물질과 염분이 있으며, 정확한 측정을 위해서는 부식에 강하고 고정밀 측정이 가능한 센서가 필요합니다. ",
                "NTU(Nephelometric Turbidity Unit)"
            )
        }
        WATER_QUALITY.QualityType.rtmWqChpla -> {
            Pair("해수 클로로필(엽록소)은 해양 생태계의 일차 생산력을 나타내는 지표로, 주로 클로로필-a를 측정하며, 식물플랑크톤의 양과 관련이 있습니다. \n" +
                    "해수 클로로필 농도를 측정하기 위해 용매를 이용한 흡광광도법이나 형광 측정법, 또는 위성 관측 등을 활용하며, \n" +
                    "이 수치는 해수의 수질 및 영양 상태를 파악하는 데 중요하게 사용됩니다.",
                "mg/m³(밀리그램/세제곱미터)")
        }
        WATER_QUALITY.QualityType.rtmWqSlnty -> {
            Pair("바다의 평균 염분 농도는 약 3.5%이며, 바닷물 1kg당 염분이 35g 녹아있습니다. \n" +
                    "이는 1,000에 대한 비율로 나타내는 35‰(퍼밀)로 표시되며, 염분 농도가 가장 높은 주요 원인은 소금의 주성분인 염화나트륨입니다. \n" +
                    "바닷물의 염분 농도는 지역에 따라 다르며, 대양의 경우 일반적으로 33~37‰입니다",
                "‰(퍼밀)"
            )
        }
    }

    val caption = "해양수산부(Ministry of Oceans and Fisheries)"

    return letsPlot(data) +
            geomLine { x="CollectingTime"; y="Value"; color="ObservatoryName"} +
            scaleXDateTime(
                format = "%d %H" // 원하는 "dd HH" 형식 지정
            ) +
            labs( title = qualityType.name(), subtitle = desc.first, y= desc.second, x="관측일시", color="관측지점", caption=caption) +
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